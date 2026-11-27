import {mountPagination} from "./pagination.js";
import {analyticsMarkup,mountAnalytics,recordNavigation} from "./atlas-analytics.js";
import {catalogPayload,catalogRecord,collections,request} from "./catalog-api.js";
import {adminColumns,adminGroups} from "./admin-data.js";
import {cookieLabel,rememberLanguage,rememberedChoice,rememberChoice,updateCookieBanner} from "./cookie-banner.js";
import {liveCategories,loadCasablancaPlaces,loadViewportPlaces} from "./live-places.js";
import {cities} from "./assets/cities.js";
import {regions} from "./assets/borders.js";
import {paint} from "./artwork.js";
import {modules,schemas,adminSections} from "./data.js";
import {$,normalize,lang,t,esc,field,input,press,link,searchMatch,values} from "./ui.js";

const root=$("app");
const dialog=$("dialog");
const state={records:[],user:null,guest:false};
try{sessionStorage.removeItem("atlas-frontend-preview-v1");}catch{}
document.cookie="smartcity_demo_student=; Max-Age=0; Path=/; SameSite=Lax";
const apiStatus={};
const connected=section=>Object.hasOwn(collections,section);
const keepLocal=record=>record.atlasCity;
for(const city of cities){
  state.records.push({id:"atlas-"+city.id,module:"cities",city:city.id,name:city.name,country:"Morocco",
    latitude:city.center[0],longitude:city.center[1],atlasCity:true});
}

let loadSequence=0;
let personalCvs=[];
let cvStatus="";
let favouriteStatus="";
let favourites=new Set();
async function loadCatalog(){
  const sequence=++loadSequence;
  if(view.name==="admin"&&(!state.user?.admin||["analytics","activity"].includes(view.params.get("tab"))))return;
  const admin=view.name==="admin";
  const account=view.name==="account";
  const tab=view.params.get("tab");
  if(account&&(!canUse()||!["cv","favourites","bookings","applications"].includes(tab)))return;
  if(account&&tab==="cv"&&view.params.get("edit")==="new")return;
  const entries=Object.entries(collections).flatMap(([section,url])=>{
    if(section==="activity")return [];
    if(section==="bookings"||section==="applications"){
      if(admin)return [[section,url]];
      return account&&canUse()&&tab===section?[[section,url+"/mine"]]:[];
    }
    return [[section,section==="news"&&admin?url+"/admin":url]];
  });
  if(canUse()&&(Object.hasOwn(modules,view.name)||view.name==="search"||(account&&tab==="favourites"))){
    entries.push(["favourites","favourites"]);
    favouriteStatus="Loading favourites…";
  }
  if(account&&tab==="cv"&&canUse()){
    entries.push(["cv","cvs/mine"]);
    cvStatus="Loading CVs…";
  }
  entries.forEach(([section])=>{apiStatus[section]="Loading records…";});
  render();
  const responses=await Promise.allSettled(entries.map(([,url])=>request(url)));
  if(sequence!==loadSequence)return;
  const records=[];
  entries.forEach(([section],index)=>{
    const result=responses[index];
    const error=result.status==="rejected"?"Could not load records. "+result.reason.message:"";
    apiStatus[section]=error;
    if(section==="favourites"){
      favouriteStatus=error;
      favourites=new Set(error?[]:result.value.map(item=>item.module+"-"+item.recordId));
    }else if(section==="cv"){
      cvStatus=error;
      personalCvs=error?[]:result.value;
    }else if(!error){
      for(const row of result.value){
        if((section==="bookings"&&!row.eventId)||(section==="applications"&&!row.sampleSubmission))continue;
        records.push(catalogRecord(section,row,records));
      }
    }
  });
  state.records=[...state.records.filter(keepLocal),...records];
  render();
}

let view;
let map;
let markers;
let disposeViewport;
let refreshViewport;
let stopCitySearch;
let formHandler;
let pending;
let travelTimer;
let walkTimer;
let preview;
let notice="";



const label=key=>esc(t(key));
const info=text=>'<p class="notice">'+label(text)+'</p>';
const tags=(title,description="")=>'<header class="page-head"><div><h1>'+label(title)+'</h1>'+(description?'<p>'+label(description)+'</p>':'')+'</div></header>';
const page=(body)=>'<div class="wrap">'+body+'</div>';
const directoryCities=()=>[...new Map(state.records.filter(row=>row.module==="cities").map(row=>[row.city,row])).values()].map(row=>{
  const original=cities.find(city=>city.id===row.city);
  const center=Number.isFinite(row.latitude)&&Number.isFinite(row.longitude)
    ?[row.latitude,row.longitude]:original?.center;
  return {...original,id:row.city,name:row.name,center,landmark:original?.landmark||row.name};
}).filter(city=>city.center);
const cityById=key=>directoryCities().find(city=>city.id===key);
const defaultCity=()=>cityById("casablanca")||directoryCities()[0];

const inAdmin=()=>view.name==="admin";
const canUse=()=>Boolean(state.user);
const auditRows=new Map();
const byId=key=>state.records.find(record=>record.id===key)||auditRows.get(key);
const article=(title,body)=>'<section class="panel stack"><h2>'+label(title)+'</h2>'+body+'</section>';
const statusLine=()=>notice?info(notice):"";

function path(name,params={}){
  const query=new URLSearchParams();
  for(const [key,value] of Object.entries(params)) if(value!=null&&value!=="") query.set(key,value);
  return (name==="home"?"index":name)+".html"+(query.size?"?"+query:"");
}
function cityPath(name,params={}){
  return path(name,{city:(view?.name==="home"?preview?.id:view?.city?.id) || "casablanca",...params});
}
function readView(){
  const url=new URL(location.href);
  const filename=url.pathname.split("/").pop().replace(/\.html$/,"");
  const name=filename===""||filename==="index"?"home":filename;
  return {name,params:url.searchParams,city:cityById(url.searchParams.get("city")) || cityById(rememberedChoice("city")) || defaultCity()};
}
function navigate(href,replace=false){
  const url=new URL(href,location.href);
  history[replace?"replaceState":"pushState"]({}, "",url.pathname+url.search+url.hash);
  dialog.close();
  render(true);
  recordNavigation(view.name);
  loadCatalog();
}
function notification(text){
  notice=text;
  render();
}
function change(language){
  rememberLanguage(language);
  document.documentElement.lang=language;
  document.documentElement.dir=language==="ar"?"rtl":"ltr";
  render();
}

function renderChrome(){
  const nav=[
    ["Home",path("home"),"home"],["City walk",cityPath("city"),"city"],
    ...Object.entries(modules).map(([key,meta])=>[meta.label,cityPath(key),key]),
    ["Account",cityPath("account"),"account"],["Administration",cityPath("admin"),"admin"]
  ];
  $("header").innerHTML=link("Atlas of Morocco",path("home"),"brand")+
    '<nav aria-label="'+label("City modules")+'">'+nav.map(([title,url,name])=>
      '<a href="'+esc(url)+'" data-page="'+esc(name)+'"'+(view.name===name?' aria-current="page"':'')+'>'+label(title)+'</a>'
    ).join("")+'</nav><div class="toolbar">'+link("Search",cityPath("search"))+
    '<label><span class="sr-only">'+label("Language")+'</span><select id="language"><option value="en">English</option><option value="fr">Français</option><option value="ar">العربية</option></select></label></div>';
  $("language").value=lang();
  $("language").onchange=event=>change(event.target.value);
  $("footer").innerHTML='<span>'+label("Atlas of Morocco")+'</span><nav>'+
    link("Terms of Service",path("terms"))+link("Privacy Policy",path("privacy"))+'</nav>';
  const preferences=document.createElement("button");
  preferences.type="button";
  preferences.className="cookie-settings";
  preferences.textContent=cookieLabel(lang());
  preferences.onclick=()=>updateCookieBanner(lang(),preferences);
  $("footer").querySelector("nav").append(preferences);
  $("dialog-close").textContent=t("Close");
  updateCookieBanner(lang());
}

function render(focus=false){
  stopCitySearch?.();
  disposeViewport?.();
  map?.remove();
  map=undefined;
  markers=undefined;
  view=readView();
  renderChrome();
  document.title=(modules[view.name]?.label || ({home:"Atlas of Morocco",city:view.city.name,account:"Account",admin:"Administration",terms:"Terms of Service",privacy:"Privacy Policy",search:"Search"}[view.name]) || "Atlas of Morocco")+" | Atlas";
  if(view.name==="home") renderHome();
  else if(view.name==="city") renderCity();
  else if(view.name==="search"||Object.hasOwn(modules,view.name)) renderCatalog();
  else if(view.name==="account") renderAccount();
  else if(view.name==="admin") renderAdmin();
  else if(["terms","privacy"].includes(view.name)) renderLegal(view.name);
  else { root.innerHTML=page(tags("Page unavailable")+link("Home",path("home"),"action")); }
  if(focus){
    window.scrollTo(0,0);
    const heading=root.querySelector("h1");
    if(heading){heading.tabIndex=-1;heading.focus({preventScroll:true});}
  }
}
function showDialog(title,html,submit){
  $("dialog-body").innerHTML='<h2>'+label(title)+'</h2>'+html;
  formHandler=submit;
  if(!dialog.open) dialog.showModal();
  $("dialog-body").querySelector("input,select,textarea,button,a")?.focus();
}
function simpleForm(items,record={},submit="Save",required=[]){
  return '<form data-form="dialog"><div class="fields">'+items.map(item=>field(item,record[item.key],required.includes(item.key))).join("")+
    '</div><div class="actions"><button class="primary">'+label(submit)+'</button></div><p class="form-status" role="status"></p></form>';
}
function formStatus(form,text){ form.querySelector(".form-status").textContent=t(text); }
function confirmDialog(title,name,callback){
  showDialog(title,'<p>'+esc(name)+'</p><form data-form="dialog"><div class="actions"><button class="danger">'+label("Delete")+'</button></div></form>',()=>{
    dialog.close();
    callback();
  });
}
function accessGate(returnTo,callback,allowGuest=false){
  pending={returnTo,callback};
  showDialog(allowGuest?"Explore the city":"Sign in",'<div class="stack">'+
'<div class="actions">'+
    link("Sign in",path("account",{tab:"signin",returnTo}),"action primary")+
    link("Register",path("account",{tab:"register",returnTo}),"action")+
    (allowGuest?press("Continue as guest","guest"):"")+'</div></div>');
}
function requireUser(action){
  if(canUse()){action();return;}
  accessGate(location.pathname+location.search,action);
}

function citySearchForm(city,id){
  const title=label("Search all city modules");
  return '<form data-form="global-search" class="toolbar" action="'+esc(path("search"))+'" method="get">'+
    '<input type="hidden" name="city" value="'+esc(city.id)+'">'+
    '<label class="sr-only" for="'+esc(id)+'">'+title+'</label>'+
    '<input id="'+esc(id)+'" name="q" type="search" placeholder="'+title+'">'+
    '<button type="submit">'+label("Search")+'</button></form>';
}

function renderHome(){
  preview=cityById(preview?.id)||cityById(rememberedChoice("city"))||defaultCity();
  const regionPaths=regions.map(([fill,d])=>'<path class="region" fill="'+fill+'" d="'+d+'"/>').join("");
  const townPaths=regions.map(([, ,d])=>'<path class="municipal" d="'+d+'"/>').join("");
  const pins=directoryCities().map(city=>{
    const [lat,lon]=city.center;
    const x=45+(lon+18)*32,y=25+(36.7-lat)*28;
    return '<button class="pin" type="button" data-action="enter" data-city="'+city.id+'" style="left:'+x/6+'%;top:'+y/5+'%" aria-label="'+esc(city.name+" · "+city.landmark)+'"><span>'+esc(city.name)+'</span></button>';
  }).join("");
  root.innerHTML=page('<section class="hero"><div><p class="eyebrow">'+label("City modules")+'</p>'+
    '<h1>'+label("Discover")+'<br><em>'+label("Morocco.")+'</em></h1>'+
    '<p>'+label("Explore landmarks, city streets and local services.")+'</p>'+
    '<label for="choose-city">'+label("Choose a city")+'</label><div class="toolbar"><select id="choose-city">'+
    directoryCities().sort((a,b)=>a.name.localeCompare(b.name)).map(city=>'<option value="'+city.id+'">'+esc(city.name)+'</option>').join("")+
    '</select>'+press("Enter city","enter","","primary")+'</div>'+
    citySearchForm(preview,"atlas-search")+
    '</div><div class="atlas-map">'+
    '<svg viewBox="0 0 600 500" role="img" aria-label="Morocco regional and municipal borders">'+regionPaths+townPaths+
    '<text class="sea-label" x="100" y="165" transform="rotate(-27 100 165)">Atlantic Ocean</text></svg>'+pins+
    '<figure class="landmark"><canvas id="landmark" width="96" height="58" aria-hidden="true"></canvas><figcaption id="landmark-caption" aria-live="polite"></figcaption></figure>'+
    '<a class="credit" href="https://github.com/zkousama/morocco-communes" target="_blank" rel="noopener noreferrer">Boundary data</a></div></section>'+
    '<section class="chapter"><h2>'+label("City modules")+'</h2><nav id="home-modules"></nav></section>');
  const picker=$("choose-city");
  picker.onchange=()=>{rememberChoice("city",picker.value);showPreview(cityById(picker.value));};
  root.querySelectorAll(".pin").forEach(pin=>{
    const display=()=>showPreview(cityById(pin.dataset.city));
    pin.addEventListener("pointerenter",display);
    pin.addEventListener("focus",display);
  });
  showPreview(preview);
}
function showPreview(city){
  preview=city;
  $("choose-city").value=city.id;
  root.querySelector('[data-form="global-search"] [name="city"]').value=city.id;
  $("landmark-caption").textContent=city.landmark+" · "+city.name;
  paint($("landmark"),city);
  root.querySelectorAll(".pin").forEach(pin=>pin.classList.toggle("current",pin.dataset.city===city.id));
  $("header").querySelectorAll("[data-page]").forEach(anchor=>{
    const page=anchor.dataset.page;
    if(page!=="home") anchor.href=path(page,{city:city.id});
  });
  $("home-modules").innerHTML=Object.entries(modules).map(([key,meta])=>
    link(meta.label,path(key,{city:city.id}),"action")
  ).join("");
}
function enterCity(city){
  if(travelTimer||!city) return;
  rememberChoice("city",city.id);
  const href=path("city",{city:city.id});
  if(matchMedia("(prefers-reduced-motion: reduce)").matches){navigate(href);return;}
  $("arrival").textContent=city.name;
  $("tunnel").hidden=false;
  travelTimer=setTimeout(()=>{
    navigate(href);
    setTimeout(()=>{$("tunnel").hidden=true;travelTimer=undefined;},240);
  },440);
}

function recordsFor(key,city){
  const rows=state.records.filter(record=>record.city===city.id && record.published!==false &&
    (key==="search"?Object.hasOwn(modules,record.module)||record.module==="news":record.module===key || key==="business"&&record.module==="news"));
  return rows;
}
function getRecord(key){
  return byId(key) || recordsFor("tourism",view.city).find(record=>record.id===key);
}
function recordUrl(record){
  return path(record.module==="news"?"business":record.module,{city:record.city,record:record.id});
}
function resultItems(records){
  if(!records.length) return '<p class="muted">'+label("Nothing to display.")+'</p>';
  return records.map(record=>'<article class="record"><div><small>'+label(record.category||modules[record.module]?.label||"")+
'</small><h3>'+esc(record.name)+'</h3>'+
    '<p>'+esc(record.description||[record.venue,record.date,record.employer,record.status].filter(Boolean).join(" · "))+'</p></div>'+
    link("Details",recordUrl(record),"action")+'</article>').join("");
}
function filterFields(records,kind){
  const options=(key)=>["All",...new Set([
    ...records.map(record=>record[key]).filter(Boolean),
    ...(key==="category"?liveCategories(kind==="search"?null:kind):[])
  ])];
  return '<div class="filters">'+field(input("query","Search","search"),view.params.get("q")||"")+
    field(input("category","Category","select",options("category")),
      kind!=="search"&&options("category").includes(rememberedChoice("category"))?rememberedChoice("category"):"All")+
    field(input("area","Area","select",options("area")),"All")+
    (kind==="sports"?field(input("date","Event date","date")):"")+
    (kind==="jobs"?field(input("employment","Employment type","select",options("employment")),"All"):"")+
    (kind==="search"?field(input("module","City modules","select",[{value:"All",label:"All"},...Object.entries(modules).map(([value,meta])=>({value,label:meta.label}))]),"All"):"")+
    '<button type="submit" class="action">'+label("Search")+'</button></div>';
}
function catalogStatus(section){
  const relevant=section==="search"?[...Object.keys(modules),"news"]:section==="business"?["business","news"]:[section];
  return [...new Set(relevant.map(key=>apiStatus[key]).filter(Boolean))].join(" ");
}
function renderCatalog(){
  const key=view.name;
  const title=modules[key]?.label || "Search";
  const selected=view.params.get("record");
  const record=selected?getRecord(selected):null;
  if(selected&&record&&record.city===view.city.id&&record.published!==false&&
      (record.module===key||key==="business"&&record.module==="news"||key==="search")){
    renderRecord(record,title);return;
  }
  const records=recordsFor(key,view.city);
  root.innerHTML=page(link("City walk",cityPath("city"),"back")+tags(title,view.city.name)+
    statusLine()+(catalogStatus(key)?info(catalogStatus(key)):selected?info("Record unavailable."):"")+'<form id="filters">'+filterFields(records,key)+'</form>'+
    '<div class="tabs">'+press("List","show-list",'id="list-toggle" aria-pressed="true"')+
    press("Map","show-map",'id="map-toggle" aria-pressed="false"')+'</div>'+
    '<p id="result-count" class="results-count" role="status"></p><div id="catalog-list"></div>'+
    (view.city.id==="casablanca"&&key!=="jobs"?'<section id="citywide" hidden><h2>Across Casablanca</h2><p id="citywide-status" role="status"></p><div id="citywide-results"></div></section>':"")+
    '<div id="catalog-map" hidden><div class="layout"><div class="map-shell"><div id="leaf-map" class="leaf-map"></div></div>'+viewportPanel()+'</div></div>');
  const filters=$("filters");
  const pager=mountPagination($("catalog-list"),{name:"Directory results",key:"directory-"+key,renderRows:resultItems});
  const filterRecord=(record,filters,matcher=searchMatch)=>{
    const {query,category,area,date,employment,module}=filters;
    return matcher(query,record)&&(category==="All"||record.category===category)&&(area==="All"||record.area===area)&&
      (!date||record.date===date)&&(!employment||employment==="All"||record.employment===employment)&&
      (!module||module==="All"||record.module===module||(module==="business"&&record.module==="news"));
  };
  const currentFilters=()=>Object.fromEntries(new FormData(filters));
  const resultsForFilters=()=>{
    const values=currentFilters();
    return records.filter(record=>filterRecord(record,values));
  };
  const liveMatches=record=>(key==="search"||record.module===key)&&filterRecord(record,currentFilters());
  const update=()=>{
    const results=resultsForFilters();
    pager.set(results,JSON.stringify(currentFilters()));
    $("result-count").textContent=results.length+" "+t("Results").toLowerCase();
    if(!$("catalog-map").hidden) {
      mapRows(results);
      attachViewport(()=>resultsForFilters(),liveMatches);
      refreshViewport?.();
    }
    updateCitywide();
    return results;
  };
  const updateCitywide=setupCitywideSearch(key,filters,currentFilters,filterRecord,resultsForFilters);
  filters.onsubmit=event=>{
    event.preventDefault();
    if(!$("catalog-map").hidden&&$("citywide")) $("list-toggle").click();
    else update();
  };
  filters.oninput=event=>{
    if(event.target.name==="category")rememberChoice("category",event.target.value==="All"?null:event.target.value);
    update();
  };
  $("map-toggle").onclick=()=>{
    $("catalog-map").hidden=false;
    $("catalog-list").hidden=true;
    $("map-toggle").setAttribute("aria-pressed","true");
    $("list-toggle").setAttribute("aria-pressed","false");
    update();
    map?.invalidateSize();
  };
  $("list-toggle").onclick=()=>{
    disposeViewport?.();
    $("catalog-map").hidden=true;
    $("catalog-list").hidden=false;
    $("map-toggle").setAttribute("aria-pressed","false");
    $("list-toggle").setAttribute("aria-pressed","true");
    update();
  };
  if(view.params.get("mode")==="map") {
    $("map-toggle").click();
    const selected=getRecord(view.params.get("focus"));
    if(map && selected?.city===view.city.id && hasLocation(selected)) {
      map.setView([selected.latitude,selected.longitude],17);
    }
  } else update();
}
function citywideMatch(query,record){
  const text=normalize([record.name,record.aliases,record.category,record.kind,modules[record.module]?.label].join(" "));
  const equivalents={
    university:"university universite",universities:"university",universites:"university",
    ecole:"school",ecoles:"school",museum:"museum musee",musee:"museum",musees:"museum",
    cafe:"cafe",cafes:"cafe"
  };
  return normalize(query).split(/\s+/).every(word=>{
    const variants=[word,...(Object.hasOwn(equivalents,word)?equivalents[word]:"").split(" ").filter(Boolean)];
    if(word.length>4&&word.endsWith("s"))variants.push(word.slice(0,-1));
    return variants.some(term=>text.includes(term));
  });
}

function liveDetails(record){
  const {latitude,longitude}=record;
  showDialog(record.name,'<p>'+label(record.category)+'</p><p>Source: OpenStreetMap</p>'+
    '<a href="https://www.openstreetmap.org/?mlat='+latitude+'&amp;mlon='+longitude+
    '#map=19/'+latitude+'/'+longitude+'" target="_blank" rel="noopener noreferrer">View on OpenStreetMap</a>');
}

function setupCitywideSearch(key,form,currentFilters,filterRecord,getCurated){
  const section=$("citywide");
  if(!section)return ()=>{};
  const status=$("citywide-status");
  const results=$("citywide-results");
  let tileResults;
  let active;
  let timer;
  let failed=false;
  let progress="";
  let pager;
  const visible=()=>!section.hidden;

  function display(){
    if(!visible())return;

    if(!tileResults){
      results.replaceChildren();
      status.textContent=failed?"Live citywide search is unavailable. The Atlas listings above are still available.":
        progress||"Looking for places across Casablanca…";
      if(failed){
        const retry=document.createElement("button");
        retry.type="button";
        retry.textContent="Retry search";
        retry.onclick=()=>{
          failed=false;
          display();
          load();
        };
        results.append(retry);
      }
      return;
    }
    const current=currentFilters();
    const curatedNames=new Set(getCurated().map(record=>normalize(record.name)));
    const matches=tileResults.places.filter(record=>
      (key==="search"||record.module===key)&&!curatedNames.has(normalize(record.name))&&
      filterRecord(record,current,citywideMatch)
    );
    status.textContent=matches.length+" matching OpenStreetMap places."+
      (tileResults.failed?" Some parts of the city could not be loaded.":"");
    pager??=mountPagination(results,{name:"OpenStreetMap search results",key:"citywide",
      renderRows:rows=>placeRows(rows.map(record=>({record,live:true})))});
    pager.set(matches,JSON.stringify(current));
  }

  async function load(){
    if(active||tileResults)return;
    const request=new AbortController();
    active=request;
    progress="";
    try{
      tileResults=await loadCasablancaPlaces(request.signal,(done,total)=>{
        progress="Looking across Casablanca ("+done+" of "+total+" map tiles)…";
        if(!request.signal.aborted&&visible())status.textContent=progress;
      });
    }catch{
      if(!request.signal.aborted)failed=true;
    }finally{
      active=undefined;
      if(!request.signal.aborted)display();
    }
  }
  form.querySelector('[name="query"]').placeholder="Search across Casablanca";
  stopCitySearch=()=>{
    clearTimeout(timer);
    active?.abort();
    stopCitySearch=undefined;
  };
  return ()=>{
    const filters=currentFilters();
    const query=normalize(filters.query);
    section.hidden=!$("catalog-map").hidden||(query.length<2&&filters.category==="All");
    clearTimeout(timer);
    if(!visible())return;
    display();
    if(!active&&!tileResults&&!failed)timer=setTimeout(load,350);
  };
}

const hasLocation=record=>Number.isFinite(record.latitude)&&Number.isFinite(record.longitude)&&
  Math.abs(record.latitude)<=90&&Math.abs(record.longitude)<=180;

function renderCity(){
  const items=recordsFor("search",view.city);
  root.innerHTML=page(link("Back to Morocco",path("home"),"back")+tags(view.city.name,"City walk")+
    citySearchForm(view.city,"city-walk-search")+
    '<div class="tabs">'+Object.entries(modules).map(([key,meta])=>link(meta.label,cityPath(key),"action")).join("")+'</div>'+
    '<div class="layout"><section><div class="map-shell"><div id="leaf-map" class="leaf-map" aria-label="'+label("Street map")+'"></div>'+
    '<div class="avatar" id="avatar" role="img" aria-label="Your character"><i></i><b></b><span></span></div></div>'+
    '<div class="map-controls"><span>'+label("Walk with arrow keys or select a point on the map.")+'</span>'+
    [["left","←"],["up","↑"],["down","↓"],["right","→"]].map(([dir,symbol])=>press(symbol,"walk",'data-direction="'+dir+'" aria-label="Walk '+dir+'"')).join("")+
    '</div></section>'+viewportPanel()+'</div>');
  mapRows(items,true);
  attachViewport(()=>items,()=>true);
}
function mapRows(records,walkable=false){
  const target=$("leaf-map");
  if(!window.L){target.textContent="The street map is unavailable.";return;}
  if(!map){
    map=L.map(target,{scrollWheelZoom:false,keyboard:!walkable,zoomControl:true,preferCanvas:true}).setView(view.city.center,walkable?17:16);
    L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png",{
      maxZoom:19,attribution:'&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);
    markers=L.layerGroup().addTo(map);
    if(walkable) map.on("click",event=>walkTo(event.latlng));
  }
  markers.clearLayers();
  for(const record of records.filter(hasLocation)){
    const content=document.createElement("div");
    const title=document.createElement("strong");
    title.textContent=record.name;
    const details=document.createElement("a");
    details.href=recordUrl(record);
    details.textContent=t("Details");
    content.append(title,document.createElement("br"),details);
    L.marker([record.latitude,record.longitude],{
      title:record.name,icon:L.divIcon({html:'<span class="marker"></span>',className:"",iconSize:[18,18]})
    }).addTo(markers).bindPopup(content);
  }
}
function viewportPanel(){
  return article("Places in view",'<p id="viewport-status" role="status">Loading the map…</p><div id="viewport-results" class="viewport-results"></div>');
}

function placeRows(rows){
  const result=document.createDocumentFragment();
  if(!rows.length){
    const empty=document.createElement("p");
    empty.textContent=t("Nothing to display.");
    result.append(empty);
  }
  for(const {record,live} of rows){
    if(!live){
      const wrapper=document.createElement("div");
      wrapper.innerHTML=resultItems([record]);
      result.append(...wrapper.children);
      continue;
    }
    const row=document.createElement("article");
    row.className="record";
    const copy=document.createElement("div");
    const category=document.createElement("small");
    category.textContent=t(record.category)+" · OpenStreetMap";
    const title=document.createElement("h3");
    title.textContent=record.name;
    copy.append(category,title);
    const detail=document.createElement("button");
    detail.type="button";
    detail.textContent=t("Details");
    detail.onclick=()=>liveDetails(record);
    row.append(copy,detail);
    result.append(row);
  }
  return result;
}

function attachViewport(getCurated,allowLive){
  if(disposeViewport)return;
  if(!map){
    $("viewport-status").textContent="The map is unavailable.";
    return;
  }
  const currentMap=map;
  const liveMarkers=L.layerGroup().addTo(currentMap);
  const results=$("viewport-results");
  const pager=mountPagination(results,{name:"Map places",key:"viewport",renderRows:placeRows});
  const status=$("viewport-status");
  let places=[];
  let controller;
  let timer;
  let disposed=false;

  const display=()=>{
    const bounds=currentMap.getBounds();
    const curated=getCurated().filter(record=>hasLocation(record)&&bounds.contains([record.latitude,record.longitude]));
    const names=new Set(curated.map(record=>normalize(record.name)));
    const visible=places.filter(record=>allowLive(record)&&!names.has(normalize(record.name)));
    pager.set([...curated.map(record=>({record,live:false})),...visible.map(record=>({record,live:true}))],
      bounds.toBBoxString());
    liveMarkers.clearLayers();
    for(const record of visible){
      const show=()=>liveDetails(record);
      L.circleMarker([record.latitude,record.longitude],{
        radius:5,weight:1,color:"#fffefa",fillColor:"#236044",fillOpacity:1
      }).addTo(liveMarkers).on("click",show);
    }
  };
  const schedule=()=>{
    clearTimeout(timer);
    controller?.abort();
    places=[];
    display();
    if(currentMap.getZoom()<15){
      status.textContent="Zoom in to see live OpenStreetMap places.";
      return;
    }
    if(view.name==="jobs"){
      status.textContent="Showing job locations in the current map area.";
      return;
    }
    status.textContent="Loading places in this area…";
    timer=setTimeout(async ()=>{
      const request=new AbortController();
      controller=request;
      try {
        const data=await loadViewportPlaces(currentMap.getBounds(),request.signal);
        if(disposed||request.signal.aborted)return;
        places=data.places;
        display();
        status.textContent=data.tooWide?"Zoom in to see live OpenStreetMap places.":
          "Showing places in the current map area. Live data: OpenStreetMap via OpenFreeMap.";
      } catch {
        if(!disposed&&!request.signal.aborted)status.textContent="Live places are unavailable. Move the map to retry.";
      }
    },350);
  };
  currentMap.on("moveend",schedule);
  currentMap.attributionControl?.addAttribution('<a href="https://openfreemap.org/">OpenFreeMap</a> · <a href="https://openmaptiles.org/">OpenMapTiles</a>');
  refreshViewport=display;
  disposeViewport=()=>{
    disposed=true;
    clearTimeout(timer);
    controller?.abort();
    currentMap.off("moveend",schedule);
    liveMarkers.remove();
    refreshViewport=undefined;
    disposeViewport=undefined;
  };
  schedule();
}

function walkTo(target){
  if(!map || view.name!=="city") return;
  $("avatar")?.classList.add("walking");
  clearTimeout(walkTimer);
  walkTimer=setTimeout(()=>$("avatar")?.classList.remove("walking"),490);
  map.panTo(target,{animate:!matchMedia("(prefers-reduced-motion: reduce)").matches,duration:.18});
}
const vectors={left:[-26,0],right:[26,0],up:[0,-26],down:[0,26]};
function walk(direction){
  if(!map||!vectors[direction])return;
  const [x,y]=vectors[direction];
  const position=map.latLngToContainerPoint(map.getCenter());
  walkTo(map.containerPointToLatLng([position.x+x,position.y+y]));
}
function details(record,schema=schemas[record.module]||[]){
  return '<dl>'+schema.filter(({key})=>key!=="file"&&key!=="name"&&record[key]!=null&&record[key]!=="")
    .map(({key,label:title})=>{
      const value=record[key];
      const text=typeof value==="boolean"?(value?"Yes":"No"):t(String(value));
      const external=key==="website"&&/^https?:\/\//i.test(value)
        ?'<a href="'+esc(value)+'" target="_blank" rel="noopener noreferrer">'+esc(text)+'</a>':esc(text);
      return '<dt>'+label(title)+'</dt><dd>'+external+'</dd>';
    }).join("")+'</dl>';
}
function renderRecord(record,title){
  const saved=favourites.has(record.id);
  const actionAttribute='data-id="'+esc(record.id)+'"';
  root.innerHTML=page(link(title,cityPath(view.name),"back")+tags(record.name,record.category)+statusLine()+
    '<section class="panel detail">'+details(record)+
    (record.module==="business"?'<h2>News and announcements</h2><div id="business-news-list"></div>':"")+
    '<div class="actions">' +press(saved?"Remove favourite":"Save favourite","favourite",actionAttribute+(favouriteStatus==="Loading favourites…"?" disabled":""))+
    (record.module==="sports"&&record.slotId&&record.availability>0?press("Reserve tickets","reserve",actionAttribute,"primary"):"")+
    (record.module==="sports"&&!record.slotId?'<span>Reservations open after a venue slot is assigned.</span>':"")+
    (record.module==="sports"&&record.slotId&&!record.capacity?'<span>Reservations open after capacity is confirmed.</span>':"")+
    (record.module==="jobs"?press("Apply","apply",actionAttribute,"primary"):"")+
    (hasLocation(record)?link("View on map",cityPath(view.name,{mode:"map",focus:record.id}),"action"):"")+'</div></section>');
  if(record.module==="business"){
    mountPagination($("business-news-list"),{name:"Business news",key:"business-news",renderRows:resultItems})
      .set(state.records.filter(item=>item.module==="news"&&item.businessRef===record.id&&item.published));
  }
}

function chooseAction(action,record){
  if(!record)return;
  if(action==="favourite"){
    requireUser(()=>updateFavourite(record));
    return;
  }
  if(action==="reserve") requireUser(()=>reservation(record));
  if(action==="apply") requireUser(()=>application(record));
}
async function updateFavourite(record){
  if(!record.apiId)return;
  const saved=favourites.has(record.id);
  try{
    await request("favourites/"+record.module+"/"+record.apiId,saved?"DELETE":"PUT");
    if(saved)favourites.delete(record.id);
    else favourites.add(record.id);
    notification(saved?"Favourite removed.":"Favourite saved.");
  }catch(error){notification(error.message);}
}

async function submitWorkflow(form,operation){
  const button=form.querySelector("button");
  if(button.disabled)return;
  button.disabled=true;
  try{await operation();}
  catch(error){formStatus(form,error.message);}
  finally{button.disabled=false;}
}
function reservation(record){
  const schema=[input("quantity","Tickets","number")];
  showDialog("Reserve tickets",'<p>'+esc(record.name+" · "+record.availability+" available · "+record.price+" MAD each")+'</p>'+

    simpleForm(schema,{quantity:1},"Reserve",["quantity"]),form=>submitWorkflow(form,async ()=>{
      const quantity=values(form,schema).quantity;
      if(!Number.isInteger(quantity)||quantity<1)throw new Error("Enter a positive whole number of tickets.");
      await request("bookings/reservations","POST",{eventId:record.apiId,quantity});
      notice="Reservation confirmed.";
      navigate(cityPath("account",{tab:"bookings"}));
    }));
}
async function application(record){
  showDialog("Apply",info("Loading CVs…"));
  const loading=$("dialog-body").firstChild;
  try{
    const available=await request("cvs/mine");
    if(!dialog.open||$("dialog-body").firstChild!==loading)return;
    if(!available.length){
      showDialog("Apply",'<p>Upload a PDF before applying.</p>'+
        link("Upload CV",cityPath("account",{tab:"cv",edit:"new",job:record.id}),"action primary"));
      return;
    }
    const schema=[input("cvId","CV","select",available.map(cv=>({value:cv.id,label:cv.title}))),input("note","Cover note","textarea")];
    showDialog("Apply",'<p>'+esc(record.name)+'</p>'+
      simpleForm(schema,{},"Submit application",["cvId"]),form=>submitWorkflow(form,async ()=>{
        const {cvId,note}=values(form,schema);
        await request("job_applications/submissions","POST",{jobId:record.apiId,cvId:Number(cvId),coverNote:note});
        notice="Application submitted.";
        navigate(cityPath("account",{tab:"applications"}));
      }));
  }catch(error){
    if(dialog.open&&$("dialog-body").firstChild===loading)showDialog("Apply",info(error.message));
  }
}
const cvUploadFields=[input("title","CV title"),input("file","PDF","file")];
const cvDownload=(id,review=false)=>'<a class="action" href="/api/cvs/'+esc(id)+(review?'/review':'/file')+'" download>Download CV</a>';
function renderPersonalAccount(tab){
  const nav='<nav class="tabs">'+Object.entries({...(state.user?.student?{cv:"My CVs"}:{}),favourites:"Favourites",bookings:"My bookings",applications:"My applications"})
    .map(([key,title])=>link(title,cityPath("account",{tab:key}),"action")).join("")+
    link("Account",cityPath("account"),"action")+'</nav>';
  let content;
  let rows;
  let renderRows;
  const list='<div id="account-list"></div>';
  const error=tab==="cv"?cvStatus:tab==="favourites"?favouriteStatus:apiStatus[tab];
  if(tab==="cv"&&view.params.get("edit")==="new"){
    content=accountForm("Upload CV",pageForm(cvUploadFields,{},"Upload",["title","file"],"personal-cv"));
  }else if(tab==="cv"){
    rows=personalCvs;
    renderRows=items=>items.length?items.map(cv=>'<article class="record"><div><h3>'+
      esc(cv.title)+'</h3><p>'+esc(cv.filename)+'</p></div><div class="actions">'+cvDownload(cv.id)+
      press("Edit CV","edit-personal-cv",'data-id="'+cv.id+'"')+press("Delete CV","delete-personal-cv",'data-id="'+cv.id+'"',"danger")+
      '</div></article>').join(""):info("No CVs uploaded.");
    content=article("My CVs",link("Upload CV",cityPath("account",{tab:"cv",edit:"new"}),"action primary")+
      (error?info(error):list));
  }else if(tab==="favourites"){
    rows=state.records.filter(row=>favourites.has(row.id)&&row.published!==false);
    renderRows=resultItems;
    content=article("Favourites",error?info(error):list);
  }else{
    rows=state.records.filter(row=>row.module===tab);
    renderRows=items=>table(items,tab==="bookings"?["name","date","status","quantity","total"]:["name","date","status","note"],record=>{
      const attr='data-id="'+esc(record.id)+'"';
      return tab==="applications"?cvDownload(record.cvId):
        record.status==="Confirmed"?press("Cancel","cancel-booking",attr):"";
    });
    content=article(tab==="bookings"?"My bookings":"My applications",error?info(error):list);
  }
  root.innerHTML=page(tags("Account")+nav+statusLine()+content);
  if($("account-list"))mountPagination($("account-list"),{name:"Account records",key:"account-"+tab,renderRows}).set(rows);
  const file=root.querySelector('[name="file"]');
  if(file)file.accept=".pdf,application/pdf";
}
async function uploadCV(form){
  await submitWorkflow(form,async ()=>{
    await request("cvs/upload","POST",new FormData(form));
    const job=view.params.get("job");
    notice="CV uploaded.";
    navigate(job?cityPath("jobs",{record:job}):cityPath("account",{tab:"cv"}));
  });
}
function personalNav(tab){
  return '<nav class="tabs">'+Object.entries({profile:"Profile",...(state.user?.student?{cv:"My CVs"}:{}),favourites:"Favourites",
    bookings:"My bookings",applications:"My applications"}).map(([key,title])=>
    '<a class="action" href="'+esc(cityPath("account",{tab:key}))+'"'+(tab===key?' aria-current="page"':'')+
    '>'+label(title)+'</a>'
  ).join("")+press("Sign out","sign-out")+'</nav>';
}
function accountNav(){
  return '<nav class="tabs">'+Object.entries({signin:"Sign in",register:"Register"}).map(([key,title])=>
    link(title,cityPath("account",{tab:key,returnTo:view.params.get("returnTo")}),"action")
  ).join("")+'</nav>';
}
function accountReturn(){
  const raw=view.params.get("returnTo") || pending?.returnTo;
  if(!raw) return cityPath("account",{tab:"profile"});
  const url=new URL(raw,location.href);
  return url.origin===location.origin && /\/(city|tourism|education|sports|jobs|business|account|admin|search)\.html$/.test(url.pathname)
    ? url.pathname+url.search : cityPath("account",{tab:"profile"});
}
function accountForm(title,body){
  return '<section class="panel stack form-wrap"><h2>'+label(title)+'</h2>'+body+'</section>';
}
function pageForm(schema,record={},submit="Save",required=[],type="account"){
  return simpleForm(schema,record,submit,required).replace('data-form="dialog"','data-form="'+type+'"');
}
const profileFields=[
  input("name","Full name"),input("email","Email","email"),input("student","Student account","checkbox"),
  input("phone","Phone","tel"),input("birthday","Date of birth","date"),input("nationality","Nationality"),
  input("country","Country"),input("region","Region"),input("city","City"),input("address","Street address"),
  input("zipcode","Postal code"),input("currentPassword","Current password (required to change email)","password")
];
const passwordFields=[input("currentPassword","Current password","password"),
  input("newPassword","New password","password"),input("confirm","Confirm new password","password")];

function renderAccount(){
  const tab=view.params.get("tab")||(canUse()?"profile":"signin");
  if(canUse()&&["cv","bookings","applications","favourites"].includes(tab)){renderPersonalAccount(tab);return;}
  const credentials=[input("email","Email","email"),input("password","Password","password")];
  const registration=[input("name","Full name"),...credentials,input("confirm","Confirm password","password"),
    input("student","Student account","checkbox")];
  let content;
  if(!canUse()||tab==="signin"||tab==="register"){
    const registering=tab==="register";
    const form=registering?registration:credentials;
    content=accountNav()+accountForm(registering?"Register":"Sign in",
      pageForm(form,{},registering?"Register":"Sign in",form.filter(item=>item.key!=="student").map(item=>item.key),
        registering?"register":"signin"));
  }else{
    content=personalNav("profile")+
      accountForm("Edit profile",info("Turning off student status pauses CV access and new job applications. Your existing records are kept.")+
        pageForm(profileFields,state.user,"Save profile",["name","email"],"profile"))+
      accountForm("Change password",info("Use at least 12 characters. Other sessions will be signed out.")+
        pageForm(passwordFields,{},"Change password",passwordFields.map(field=>field.key),"password"));
  }
  root.innerHTML=page(tags("Account")+statusLine()+content);
}

function accountSubmit(form,type){
  if(type==="profile"||type==="password"){
    submitWorkflow(form,async ()=>{
      const data=values(form,type==="profile"?profileFields:passwordFields);
      for(const key of ["currentPassword","newPassword","confirm"]){
        if(form.elements[key])data[key]=form.elements[key].value;
      }
      if(type==="password"&&data.newPassword!==data.confirm)throw new Error("Passwords must match.");
      delete data.confirm;
      if(type==="profile"&&!data.birthday)data.birthday=null;
      state.user=await request(type==="profile"?"account":"account/password","PUT",data);
      personalCvs=[];
      notification(type==="profile"?"Profile saved.":"Password changed.");
    });
    return;
  }
  if(!["register","signin"].includes(type))return;
  submitWorkflow(form,async ()=>{
    const data=Object.fromEntries(new FormData(form));
    if(type==="register"&&data.password!==data.confirm)throw new Error("Passwords must match.");
    const {name,email,password}=data;
    state.user=await request("account/"+type,"POST",{name,email,password,student:form.elements.student?.checked===true});
    const target=accountReturn();
    const continuation=pending?.callback;
    pending=undefined;
    navigate(target);
    if(continuation)continuation();
  });
}

const tableLabels={note:"Cover note",total:"Total (MAD)",name:"Name",email:"Email",role:"Role",enabled:"Enabled",status:"Status",date:"Date",source:"Source",category:"Category",quantity:"Tickets",cv:"CV",owner:"Account",city:"City",venue:"Venue"};
function table(rows,columns,actions){
  if(!rows.length) return '<p>'+label("Nothing to display.")+'</p>';
  return '<div class="table-scroll"><table><thead><tr>'+columns.map(key=>'<th scope="col">'+label(tableLabels[key]||key)+'</th>').join("")+
    (actions?'<th scope="col">'+label("Actions")+'</th>':"")+'</tr></thead><tbody>'+rows.map(row=>'<tr>'+
    columns.map(key=>{
      const value=key==="city"?(cityById(row[key])?.name || row[key]):row[key];
      return '<td>'+label(typeof value==="boolean"?(value?"Yes":"No"):String(value??""))+'</td>';
    }).join("")+(actions?'<td><div class="actions">'+actions(row)+'</div></td>':"")+'</tr>').join("")+'</tbody></table></div>';
}
function renderAdmin(){
  if(!state.user?.admin){
    root.innerHTML=page(tags("Administration")+info("Administrator sign-in is required.")+
      link("Sign in",cityPath("account",{tab:"signin",returnTo:location.pathname+location.search}),"action"));
    return;
  }
  const tab=Object.hasOwn(adminSections,view.params.get("tab"))?view.params.get("tab"):"overview";
  const menu='<nav class="admin-nav" aria-label="Administration">'+adminGroups.map(([heading,sections])=>
    '<section><h2>'+label(heading)+'</h2>'+sections.map(id=>
      '<a href="'+esc(cityPath("admin",{tab:id}))+'"'+(id===tab?' aria-current="page"':'')+'>'+label(adminSections[id])+'</a>'
    ).join("")+'</section>'
  ).join("")+'</nav>';
  const all=section=>state.records.filter(record=>record.module===section&&!record.atlasCity);
  let body;
  if(tab==="analytics")body=analyticsMarkup;
  else if(tab==="overview"){
    const metrics=[
      ["Pending bookings","bookings",all("bookings").filter(row=>row.status==="Pending").length],
      ["Applications to review","applications",all("applications").filter(row=>row.status==="Pending").length],
      ["Sport events","sports",all("sports").length]
    ];
    const overview='<div class="stats">'+metrics.map(([name,section,count])=>
      '<a href="'+esc(cityPath("admin",{tab:section}))+'"><strong>'+count+'</strong>'+label(name)+'</a>'
    ).join("")+'</div><h3>Directory by module</h3><div class="actions">'+Object.entries(modules).map(([section,meta])=>
      link(meta.label+" ("+all(section).length+")",cityPath("admin",{tab:section}),"action")
    ).join("")+'</div>';
    body=article("Dashboard",overview)+article("Recent activity",'<div id="recent-activity"></div>'+
      link("Activity log",cityPath("admin",{tab:"activity"}),"action"));
  }else{
    const locked=["bookings","applications","activity"].includes(tab);
    const records=all(tab);
    const states=tab==="activity"?["INSERT","UPDATE","DELETE"]:[...new Set(records.map(row=>row.status).filter(Boolean))];
    const filter=states.length?field(input("admin-status","Status","select",["All",...states]),"All"):"";
    body=article(adminSections[tab],'<div class="filters">'+field(input("admin-query","Search","search"))+filter+
      (locked?"":press("Add","admin-add",'data-section="'+tab+'"',"primary"))+'</div>'+(tab==="activity"?"":'<p id="admin-count" class="results-count" role="status"></p>')+
      '<div id="admin-table"></div>');
  }
  root.innerHTML=page(tags("Administration",connected(tab)?"PostgreSQL directory":"Directory")+
    (!connected(tab)&&!["overview","analytics"].includes(tab)?info("This section is unavailable."):"")+
    (apiStatus[tab]?info(apiStatus[tab]):"")+statusLine()+
    '<div class="admin-layout">'+menu+'<div class="admin-content stack">'+body+'</div></div>');
  if(tab==="analytics"){mountAnalytics($("atlas-analytics"));return;}
  if(tab==="overview"){mountAudit($("recent-activity"),"recent-activity",false).set([]);return;}
  const search=root.querySelector('[name="admin-query"]');
  const status=root.querySelector('[name="admin-status"]');
  if(tab==="activity"){
    const pager=mountAudit($("admin-table"),"activity-log",true);
    const update=()=>pager.set([],JSON.stringify({query:search.value.trim(),operation:status.value==="All"?"":status.value}));
    search.oninput=update;
    status.onchange=update;
    update();
    return;
  }
  const pager=mountPagination($("admin-table"),{name:"Admin records",key:"admin-"+tab,
    renderRows:records=>table(records,adminColumns[tab],record=>{
      const attr='data-id="'+esc(record.id)+'" data-section="'+tab+'"';
      return press("Details","admin-details",attr)+
        (tab==="bookings"?"":press(tab==="applications"?"Update status":"Edit","admin-edit",attr))+
        (!["bookings","applications"].includes(tab)?press("Delete","admin-delete",attr,"danger"):"");
    })});
  const update=()=>{
    const records=all(tab).filter(record=>searchMatch(search.value,record)&&
      (!status||status.value==="All"||record.status===status.value));
    $("admin-count").textContent=records.length+" "+t("Results").toLowerCase();
    pager.set(records,JSON.stringify([search.value,status?.value]));
  };
  search.oninput=update;
  if(status)status.onchange=update;
  update();
}

function mountAudit(container,key,showDetails){
  auditRows.clear();
  return mountPagination(container,{
    name:showDetails?"Activity log pages":"Recent activity pages",key,
    load:async (offset,limit,filter)=>{
      const params=new URLSearchParams({offset,limit,...(filter?JSON.parse(filter):{})});
      const rows=await request(collections.activity+"?"+params);
      return rows.map(row=>catalogRecord("activity",row,[]));
    },
    renderRows:rows=>{
      auditRows.clear();
      for(const row of rows)auditRows.set(row.id,row);
      return table(rows,adminColumns.activity,showDetails?record=>press("Details","admin-details",
        'data-id="'+esc(record.id)+'" data-section="activity"'):undefined);
    }
  });
}

const locationFields=[input("address","Address"),input("latitude","Latitude","number"),input("longitude","Longitude","number")];
const description=input("description","Description","textarea");
const nameField=input("name","Name");
const catalogFields={
  cities:[nameField,input("country","Country"),description,...locationFields.slice(1)],
  tourism:[nameField,description,input("category","Category"),...locationFields,input("hours","Opening hours"),input("rating","Rating","number")],
  education:[nameField,description,input("category","Institution type"),...locationFields,input("email","Contact email","email"),input("website","Website","url")],
  venues:[nameField,description,input("category","Venue type"),...locationFields,input("capacity","Capacity","number")],
  business:[nameField,description,input("category","Business type"),input("country","Country"),input("cityName","City"),
    input("region","Region"),...locationFields,input("email","Email","email"),input("phone","Phone"),input("tax","Tax reference"),
    input("domainName","Domain name")],
  slots:[input("start","Start time","datetime-local"),input("end","End time","datetime-local")],
  sports:[nameField,description,input("capacity","Total tickets","number"),input("price","Ticket price (MAD)","number")],
  jobs:[nameField,description,input("category","Industry"),input("employment","Employment type"),input("salary","Salary (MAD)","number"),
    input("deadline","Application deadline","date"),input("requirements","Requirements","textarea")],
  news:[nameField,input("description","Body","textarea"),input("category","Type","select",["News","Announcement"]),input("published","Published","checkbox")],
  applications:[input("status","Status","select",["Pending","Accepted","Rejected"])]
};
const choicesFor=(section)=>state.records.filter(row=>row.module===section&&row.apiId)
  .map(row=>({value:row.id,label:section==="slots"?row.venue+" · "+row.name:row.name}));
const refField=(key,label,section)=>input(key,label,"select",[{value:"",label:"Choose a record"},...choicesFor(section)]);
function editCatalog(section,record){
  const schema=[...catalogFields[section]];
  const place=["tourism","education","venues"].includes(section);
  if(place)schema.push(refField("cityRef","City","cities"));
  if(section==="slots")schema.push(refField("venueRef","Venue","venues"));
  if(section==="sports"){
    const usedSlots=new Set(state.records.filter(row=>row.module==="sports"&&row.id!==record?.id).map(row=>row.slotRef));
    schema.push(input("slotRef","Venue time slot","select",[{value:"",label:"Choose a slot"},
      ...choicesFor("slots").filter(choice=>!usedSlots.has(choice.value))]));
  }
  if(section==="news")schema.push(refField("businessRef","Business","business"));
  if(section==="jobs")schema.push(refField("businessRef","Business (optional)","business"));
  const initial={cityName:view.city.name,country:"Morocco",cityRef:state.records.find(row=>row.module==="cities"&&row.apiId&&row.city===view.city.id)?.id,...record};
  const required=["name","category","country","cityRef","price","employment","salary","capacity","email","phone","cityName","start","end","status"];
  if(section==="slots")required.push("venueRef");
  if(section==="news")required.push("businessRef","description");
  if(section==="sports")required.push("slotRef");
  if(place||section==="cities")required.push("latitude","longitude");
  showDialog(section==="applications"?"Update status":record?"Edit":"Add",(section==="sports"?'<p>Choose a venue slot and set ticket capacity. '+link("Manage venue slots",cityPath("admin",{tab:"slots"}),"action")+'</p>':"")+
    simpleForm(schema,initial,"Save",required),
    async form=>{
      const submit=form.querySelector('button');
      if(submit.disabled)return;
      submit.disabled=true;
      try{
        const payload=catalogPayload(section,values(form,schema));
        if(section==="applications"){
          await request("job_applications/"+record.apiId+"/status","PATCH",{status:form.elements.status.value.toUpperCase()});
        }else{
          await request(collections[section]+(record?"/"+record.apiId:""),record?"PUT":"POST",payload);
        }
        dialog.close();
        notice="Record saved.";
        await loadCatalog();
      }catch(error){formStatus(form,error.message);}
      finally{submit.disabled=false;}
    });
}

function adminEdit(section,record){
  if(!inAdmin()||!state.user?.admin||!Object.hasOwn(catalogFields,section))return;
  if(record&&record.module!==section)return;
  if(section!=="applications"||record)editCatalog(section,record);
}
function renderLegal(kind){
  const privacy=kind==="privacy";
  root.innerHTML=page(tags(privacy?"Privacy Policy":"Terms of Service")+article(privacy?"Privacy Policy":"Terms of Service",privacy?
    '<dl><dt>Essential</dt><dd>Remembers your choices for six months.</dd>'+
    '<dt>Language</dt><dd>Remembers your selected language for future visits. You can always change language for the current tab.</dd>'+
    '<dt>Analytics</dt><dd>Off by default. When site analytics is enabled, your opt-in allows Atlas to count page views using a page identifier and timestamp.</dd>'+
    '<dt>Personalization</dt><dd>Off by default. Saves your last selected city and preferred category in this browser for future visits. These preferences are stored separately from analytics.</dd></dl>'+
    '<p>Change your choices through Cookie preferences in the footer. Turning off language or personalization clears its cookies. Atlas sends no visitor details to a location provider and stores no IP addresses, ISP data, precise locations, or individual browsing history for analytics.</p>'+
    '<p>Sign-in uses a session cookie. Account records, uploads and applications are stored on the Atlas server. Street tiles are provided by OpenStreetMap.</p>':
    '<p>Confirm opening hours, service availability and event information with the relevant provider before making plans.</p>'));
}

function auditValues(heading,raw){
  const values=Object.entries(JSON.parse(raw||"{}"));
  return values.length?'<h3>'+heading+'</h3><dl>'+
    values.map(([field,value])=>'<dt>'+esc(field)+'</dt><dd>'+esc(value??"—")+'</dd>').join("")+'</dl>':"";
}

const actionHandlers={
  enter:button=>enterCity(cityById(button.dataset.city)||preview),
  "show-list":()=>{}, "show-map":()=>{},
  guest:()=>{
    state.guest=true;
    const target=pending?.returnTo || cityPath("city");
    pending=undefined;navigate(target);
  },
  walk:button=>walk(button.dataset.direction),
  favourite:button=>chooseAction("favourite",getRecord(button.dataset.id)),
  reserve:button=>chooseAction("reserve",getRecord(button.dataset.id)),
  apply:button=>chooseAction("apply",getRecord(button.dataset.id)),
  "sign-out":async ()=>{
    try{
      await request("account/signout","POST");
      state.user=null;state.guest=false;pending=undefined;
      favourites.clear();
      auditRows.clear();
      personalCvs=[];
      state.records=state.records.filter(keepLocal);
      navigate(path("home"));
    }catch(error){notification(error.message);}
  },
  "edit-personal-cv":button=>{
    const cv=personalCvs.find(item=>String(item.id)===button.dataset.id);
    if(!cv)return;
    showDialog("Edit CV",simpleForm(cvUploadFields,cv,"Save",["title"]),
      form=>submitWorkflow(form,async ()=>{
        await request("cvs/mine/"+cv.id,"PUT",new FormData(form));
        dialog.close();
        notice="CV updated.";
        await loadCatalog();
      }));
    dialog.querySelector('[name="file"]').accept=".pdf,application/pdf";
  },
  "delete-personal-cv":button=>{
    const cv=personalCvs.find(item=>String(item.id)===button.dataset.id);
    if(!cv)return;
    confirmDialog("Delete CV?",cv.title,async ()=>{
      try{
        await request("cvs/mine/"+cv.id,"DELETE");
        notice="CV deleted.";
        await loadCatalog();
      }catch(error){notification(error.message);}
    });
  },
  "cancel-booking":button=>{
    const record=byId(button.dataset.id);
    if(record?.module!=="bookings")return;
    showDialog("Cancel reservation?",'<p>'+esc(record.name)+'</p>'+
      '<form data-form="dialog"><button class="danger">Cancel reservation</button><p class="form-status" role="status"></p></form>',
      form=>submitWorkflow(form,async ()=>{
        await request("bookings/"+record.apiId+"/cancel","POST");
        dialog.close();
        notice="Reservation cancelled.";
        await loadCatalog();
      }));
  },
  "admin-add":button=>adminEdit(button.dataset.section),
  "admin-edit":button=>adminEdit(button.dataset.section,byId(button.dataset.id)),
  "admin-details":button=>{
    if(!inAdmin())return;
    const record=byId(button.dataset.id);
    if(record?.module==="activity"){
      showDialog(record.name,'<p>'+esc(record.operation+" · "+record.date+" · "+record.source)+'</p><p>Changed fields: '+
        esc(record.description)+'</p>'+auditValues("Before",record.beforeValues)+auditValues("After",record.afterValues)+
        ((record.previousStatus||record.currentStatus)?
          '<p>'+esc([record.previousStatus,record.currentStatus].filter(Boolean).join(" → "))+'</p>':""));
      return;
    }
    if(record)showDialog(record.name,details(record)+
      (record.module==="applications"?cvDownload(record.cvId,true):""));
  },
  "admin-delete":button=>{
    const record=byId(button.dataset.id);
    if(!inAdmin()||!state.user?.admin||!record||!connected(record.module))return;
    confirmDialog("Delete record?",record.name,async ()=>{
      try{
        await request(collections[record.module]+"/"+record.apiId,"DELETE");
        notice="Record deleted.";
        await loadCatalog();
      }catch(error){notification(error.message);}
    });
  }
};

document.addEventListener("click",event=>{
  const trigger=event.target.closest("[data-action]");
  if(trigger){actionHandlers[trigger.dataset.action]?.(trigger);return;}
  const anchor=event.target.closest("a[href]");
  if(!anchor||anchor.target||event.defaultPrevented||event.button!==0||event.metaKey||event.ctrlKey||event.altKey||event.shiftKey)return;
  const url=new URL(anchor.href,location.href);
  if(url.origin!==location.origin||!url.pathname.endsWith(".html"))return;
  event.preventDefault();
  notice="";
  navigate(url.href);
});
document.addEventListener("submit",event=>{
  const type=event.target.dataset.form;
  if(!type)return;
  event.preventDefault();
  if(type==="dialog"){formHandler?.(event.target);return;}
  if(type==="personal-cv"){uploadCV(event.target);return;}
  if(type==="global-search"){
    const {city,q}=Object.fromEntries(new FormData(event.target));
    navigate(path("search",{city,q:q.trim()}));
    return;
  }
  accountSubmit(event.target,type);
});
document.addEventListener("keydown",event=>{
  const key={ArrowLeft:"left",ArrowRight:"right",ArrowUp:"up",ArrowDown:"down"}[event.key];
  if(!key||view.name!=="city"||dialog.open||event.target.closest?.("input,select,textarea,button,[contenteditable]"))return;
  event.preventDefault();walk(key);
});
window.addEventListener("popstate",()=>{dialog.close();notice="";render(true);recordNavigation(view.name);loadCatalog();});
change(lang());
recordNavigation(view.name);
request("account").then(user=>{state.user=user;render();}).catch(()=>{}).finally(()=>loadCatalog());
