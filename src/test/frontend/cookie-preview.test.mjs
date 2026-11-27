import assert from "node:assert/strict";
import {test} from "node:test";
import {registerHooks} from "node:module";
import {readFileSync} from "node:fs";

const staticRoot=new URL("../../main/resources/static/",import.meta.url).href;
registerHooks({
  load(url,context,next){
    if(url.startsWith(staticRoot)&&url.endsWith(".js")){
      return {format:"module",source:readFileSync(new URL(url)),shortCircuit:true};
    }
    return next(url,context);
  }
});

const cookies=new Map();
const storage=()=>({getItem:key=>storageValues.get(key)??null,setItem:(key,value)=>storageValues.set(key,value)});
let storageValues=new Map();
const element=()=>({hidden:false,textContent:"",focus(){},setAttribute(){}});
const buttons=Object.fromEntries(["essential","all","custom"].map(key=>[key,element()]));
const choices=Object.fromEntries(["language","analytics","personalization"].map(key=>[key,{...element(),checked:false}]));
const labels=Array.from({length:3},element);
const form={...element(),hidden:true,elements:choices,
  querySelector:()=>element(),querySelectorAll:()=>labels};
const canvas={clientWidth:360,getContext:()=>null};
const banner={...element(),querySelector(selector){
  if(selector==="form")return form;
  if(selector==="canvas")return canvas;
  const choice=selector.match(/data-choice="(.*?)"/);
  return choice?buttons[choice[1]]:element();
}};
const document=new EventTarget();
Object.defineProperty(document,"cookie",{
  get:()=>[...cookies].map(([key,value])=>key+"="+value).join("; "),
  set:raw=>{
    const [pair,...attributes]=raw.split("; ");
    const [key,value]=pair.split("=");
    if(attributes.includes("Max-Age=0"))cookies.delete(key);
    else cookies.set(key,value);
  }
});
Object.assign(document,{body:{append(){}},createElement:()=>banner});

globalThis.document=document;
globalThis.location={pathname:"/",search:"",protocol:"http:"};
globalThis.sessionStorage=storage();
globalThis.localStorage={removeItem(){}};
globalThis.window={};
globalThis.matchMedia=()=>({matches:true});
const preferencesModule=await import("../../main/resources/static/cookie-banner.js");

test("first visit, essential only, independent choices, saved preferences, and revocation",()=>{
  const {preferences,updateCookieBanner,readLanguage,rememberLanguage,rememberChoice,rememberedChoice}=preferencesModule;
  const reopen=()=>updateCookieBanner(readLanguage(),element());
  const save=()=>form.onsubmit({preventDefault(){}});
  updateCookieBanner("en");
  assert.equal(banner.hidden,false);
  assert.equal(preferences(),null);
  buttons.custom.onclick();
  assert.equal(form.hidden,false);
  assert.ok(Object.values(choices).every(choice=>!choice.checked));
  buttons.essential.onclick();
  assert.deepEqual([...cookies],[["atlas-consent","v1.000"]]);
  rememberLanguage("fr");
  assert.equal(readLanguage(),"fr");
  assert.equal(cookies.has("atlas-language"),false);

  reopen();
  choices.language.checked=true;
  choices.personalization.checked=true;
  save();
  assert.deepEqual(preferences(),{language:true,analytics:false,personalization:true});
  rememberChoice("city","rabat");
  rememberChoice("category","Landmark");
  storageValues=new Map();
  assert.equal(readLanguage(),"fr");
  assert.equal(rememberedChoice("city"),"rabat");
  assert.equal(rememberedChoice("category"),"Landmark");
  reopen();
  assert.deepEqual(Object.values(choices).map(choice=>choice.checked),[true,false,true]);
  choices.language.checked=false;
  choices.analytics.checked=true;
  choices.personalization.checked=false;
  save();
  assert.deepEqual([...cookies],[["atlas-consent","v1.010"]]);
  assert.equal(rememberedChoice("city"),null);
  rememberChoice("city","casablanca");
  assert.equal(cookies.has("atlas-city"),false);
  assert.equal(readLanguage(),"en");

  reopen();
  buttons.all.onclick();
  assert.deepEqual(preferences(),{language:true,analytics:true,personalization:true});
});

test("opt-in counts only new navigations, then revocation stops collection",async()=>{
  const {updateCookieBanner}=preferencesModule;
  const {recordNavigation}=await import("../../main/resources/static/atlas-analytics.js");
  const calls=[];
  globalThis.fetch=async (url,options)=>{
    calls.push({url,options});
    return {ok:true,json:async()=>({enabled:true})};
  };
  updateCookieBanner("en",element());
  buttons.essential.onclick();
  await recordNavigation("home");
  assert.deepEqual(calls,[]);
  updateCookieBanner("en",element());
  choices.analytics.checked=true;
  form.onsubmit({preventDefault(){}});
  await recordNavigation("home");
  assert.deepEqual(calls,[]);
  location.pathname="/tourism.html";
  await recordNavigation("tourism");
  await recordNavigation("tourism");
  location.pathname="/admin.html";
  await recordNavigation("admin");
  location.pathname="/tourism.html";
  await recordNavigation("tourism");
  updateCookieBanner("en",element());
  buttons.essential.onclick();
  location.pathname="/jobs.html";
  await recordNavigation("jobs");
  assert.deepEqual(calls.map(call=>[call.url,call.options.body]),[
    ["/api/atlas-visits/collection",undefined],
    ["/api/atlas-visits",'{"page":"TOURISM"}'],
    ["/api/atlas-visits",'{"page":"TOURISM"}']
  ]);
});

test("account route 404 identifies a stale or missing Spring Boot server",async()=>{
  const {request}=await import("../../main/resources/static/catalog-api.js");
  globalThis.fetch=async ()=>({ok:false,status:404,json:async()=>({})});
  await assert.rejects(request("account/register","POST",{}),/current Spring Boot server/);
  await assert.rejects(request("tourism_places/123"),/Request failed \(404\)/);
});

test("a stale server's account update error explains how to recover",async()=>{
  const {request}=await import("../../main/resources/static/catalog-api.js");
  for(const status of [404,405]){
    globalThis.fetch=async ()=>({ok:false,status,json:async()=>({})});
    await assert.rejects(request("account","PUT",{}),/Restart it from the current project/);
    await assert.rejects(request("account/password","PUT",{}),/Restart it from the current project/);
  }
  globalThis.fetch=async ()=>({ok:false,status:403,json:async()=>({detail:"The current password is incorrect."})});
  await assert.rejects(request("account/password","PUT",{}),/The current password is incorrect/);
});

test("record pagination supports 15 and 30 rows and clamps to the final page",async()=>{
  const {pageOf}=await import("../../main/resources/static/pagination.js");
  const rows=Array.from({length:31},(_,i)=>i+1);
  assert.deepEqual(pageOf(rows,1,15),{page:1,rows:rows.slice(0,15),hasNext:true});
  assert.deepEqual(pageOf(rows,2,15),{page:2,rows:rows.slice(15,30),hasNext:true});
  assert.deepEqual(pageOf(rows,9,30),{page:2,rows:[31],hasNext:false});
  assert.deepEqual(pageOf([],2,15),{page:1,rows:[],hasNext:false});
});

test("per-page controls reset to page one and filtering resets the page",async()=>{
  const {mountPagination}=await import("../../main/resources/static/pagination.js");
  const controls=new Map(["[data-page-rows]","select","[data-page-back]","[data-page-next]","[data-page-label]"]
    .map(key=>[key,{...element(),replaceChildren(){this.innerHTML="";}}]));
  const container={querySelector:key=>controls.get(key)};
  const rows=Array.from({length:31},(_,i)=>i+1);
  const pager=mountPagination(container,{name:"Test list",key:"tests",renderRows:items=>items.join(",")});
  const body=controls.get("[data-page-rows]");
  const next=controls.get("[data-page-next]");
  const previous=controls.get("[data-page-back]");
  const select=controls.get("select");
  await pager.set(rows,"all");
  assert.equal(body.innerHTML,rows.slice(0,15).join(","));
  assert.equal(previous.disabled,true);
  next.onclick();
  assert.equal(body.innerHTML,rows.slice(15,30).join(","));
  select.value="30";
  select.onchange();
  assert.equal(body.innerHTML,rows.slice(0,30).join(","));
  next.onclick();
  assert.equal(body.innerHTML,"31");
  assert.equal(next.disabled,true);
  await pager.set([27,28],"filtered");
  assert.equal(body.innerHTML,"27,28");
  assert.equal(previous.disabled,true);
});
