import {cities} from "./assets/cities.js";
import {regions} from "./assets/borders.js";
import {paint} from "./artwork.js";
import {modules,schemas,adminSections,seed} from "./data.js";
import {$,normalize,lang,t,esc,field,input,press,link,searchMatch,values} from "./ui.js";

const root=$("app");
const dialog=$("dialog");
const storageKey="atlas-frontend-preview-v1";
const defaultState={records:seed,user:null,guest:false,profiles:{},cvs:[],favourites:[]};
let state;
try { state=JSON.parse(sessionStorage.getItem(storageKey)) || structuredClone(defaultState); }
catch { state=structuredClone(defaultState); }
let view;
let map;
let markers;
let formHandler;
let pending;
let travelTimer;
let walkTimer;
let preview;
let notice="";
const cvFiles=new Map();

function save(){ sessionStorage.setItem(storageKey,JSON.stringify(state)); }
const id=()=>crypto.randomUUID?.() || Array.from(crypto.getRandomValues(new Uint32Array(4)),part=>part.toString(16)).join("-");
const today=()=>new Date().toISOString().slice(0,10);
const label=key=>esc(t(key));
const info=text=>'<p class="notice">'+label(text)+'</p>';
const tags=(title,description="")=>'<header class="page-head"><div><h1>'+label(title)+'</h1>'+(description?'<p>'+label(description)+'</p>':'')+'</div></header>';
const page=(body)=>'<div class="wrap">'+body+'</div>';
const cityById=key=>cities.find(city=>city.id===key);
const owner=()=>state.user?.email || "";
const isAdmin=()=>state.user?.role==="Administrator";
const canUse=()=>Boolean(state.user);
const byId=key=>state.records.find(record=>record.id===key);
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
  return {name,params:url.searchParams,city:cityById(url.searchParams.get("city")) || cityById("casablanca")};
}
function navigate(href,replace=false){
  const url=new URL(href,location.href);
  history[replace?"replaceState":"pushState"]({}, "",url.pathname+url.search+url.hash);
  dialog.close();
  render(true);
}
function notification(text){
  notice=text;
  render();
}
function log(name,description="",status="Preview"){
  state.records.unshift({id:id(),module:"activity",name,description,status,source:owner()||"Guest",date:new Date().toISOString()});
}
function change(language){
  localStorage.setItem("atlas-language",language);
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
  $("dialog-close").textContent=t("Close");
}

function render(focus=false){
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
    info("Preview data stays in this browser tab.")+'<div class="actions">'+
    link("Sign in",path("account",{tab:"signin",returnTo}),"action primary")+
    link("Register",path("account",{tab:"register",returnTo}),"action")+
    (allowGuest?press("Continue as guest","guest"):"")+'</div></div>');
}
function requireUser(action){
  if(canUse()){action();return;}
  accessGate(location.pathname+location.search,action);
}

function renderHome(){
  preview=preview || cityById("casablanca");
  const regionPaths=regions.map(([fill,d])=>'<path class="region" fill="'+fill+'" d="'+d+'"/>').join("");
  const townPaths=regions.map(([, ,d])=>'<path class="municipal" d="'+d+'"/>').join("");
  const pins=cities.map(city=>{
    const [lat,lon]=city.center;
    const x=45+(lon+18)*32,y=25+(36.7-lat)*28;
    return '<button class="pin" type="button" data-action="enter" data-city="'+city.id+'" style="left:'+x/6+'%;top:'+y/5+'%" aria-label="'+esc(city.name+" · "+city.landmark)+'"><span>'+esc(city.name)+'</span></button>';
  }).join("");
  root.innerHTML=page('<section class="hero"><div><p class="eyebrow">'+label("City modules")+'</p>'+
    '<h1>'+label("Discover")+'<br><em>'+label("Morocco.")+'</em></h1>'+
    '<p>'+label("Explore landmarks, city streets and local services.")+'</p>'+
    '<label for="choose-city">'+label("Choose a city")+'</label><div class="toolbar"><select id="choose-city">'+
    cities.slice().sort((a,b)=>a.name.localeCompare(b.name)).map(city=>'<option value="'+city.id+'">'+esc(city.name)+'</option>').join("")+
    '</select>'+press("Enter city","enter","","primary")+'</div>'+
    '<form data-form="global-search" class="toolbar"><label class="sr-only" for="atlas-search">'+label("Search")+'</label><input id="atlas-search" name="query" type="search" placeholder="'+label("Search")+'"><button>'+label("Search")+'</button></form>'+
    '</div><div class="atlas-map">'+
    '<svg viewBox="0 0 600 500" role="img" aria-label="Morocco regional and municipal borders">'+regionPaths+townPaths+
    '<text class="sea-label" x="100" y="165" transform="rotate(-27 100 165)">Atlantic Ocean</text></svg>'+pins+
    '<figure class="landmark"><canvas id="landmark" width="96" height="58" aria-hidden="true"></canvas><figcaption id="landmark-caption" aria-live="polite"></figcaption></figure>'+
    '<a class="credit" href="https://github.com/zkousama/morocco-communes" target="_blank" rel="noopener noreferrer">Boundary data</a></div></section>'+
    '<section class="chapter"><h2>'+label("City modules")+'</h2><nav id="home-modules"></nav></section>');
  const picker=$("choose-city");
  picker.onchange=()=>showPreview(cityById(picker.value));
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
  if(city.id!=="casablanca"&&(key==="tourism"||key==="search")){
    rows.push({id:"landmark-"+city.id,module:"tourism",city:city.id,name:city.landmark,category:"Landmark",
      description:"Destination in "+city.name+".",example:true});
  }
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
    (record.example?" · "+label("Sample listing"):"")+'</small><h3>'+esc(record.name)+'</h3>'+
    '<p>'+esc(record.description||[record.venue,record.date,record.employer,record.status].filter(Boolean).join(" · "))+'</p></div>'+
    link("Details",recordUrl(record),"action")+'</article>').join("");
}
function filterFields(records,kind){
  const options=(key)=>["All",...new Set(records.map(record=>record[key]).filter(Boolean))];
  return '<div class="filters">'+field(input("query","Search","search"),view.params.get("q")||"")+
    field(input("category","Category","select",options("category")),"All")+
    field(input("area","Area","select",options("area")),"All")+
    (kind==="sports"?field(input("date","Event date","date")):"")+
    (kind==="jobs"?field(input("employment","Employment type","select",options("employment")),"All"):"")+
    (kind==="search"?field(input("module","City modules","select",[{value:"All",label:"All"},...Object.entries(modules).map(([value,meta])=>({value,label:meta.label}))]),"All"):"")+
    '</div>';
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
    statusLine()+'<form id="filters">'+filterFields(records,key)+'</form>'+
    '<div class="tabs">'+press("List","show-list",'id="list-toggle" aria-pressed="true"')+
    press("Map","show-map",'id="map-toggle" aria-pressed="false"')+'</div>'+
    '<p id="result-count" class="results-count" role="status"></p><div id="catalog-list"></div>'+
    '<div id="catalog-map" hidden class="map-shell"><div id="leaf-map" class="leaf-map"></div></div>');
  const filters=$("filters");
  const update=()=>{
    const {query,category,area,date,employment,module}=Object.fromEntries(new FormData(filters));
    const results=records.filter(record=>
      searchMatch(query,record)&&(category==="All"||record.category===category)&&(area==="All"||record.area===area)&&
      (!date||record.date===date)&&(!employment||employment==="All"||record.employment===employment)&&
      (!module||module==="All"||record.module===module||(module==="business"&&record.module==="news")));
    $("catalog-list").innerHTML=resultItems(results);
    $("result-count").textContent=results.length+" "+t("Results").toLowerCase();
    if(!$("catalog-map").hidden) mapRows(results);
    return results;
  };
  filters.onsubmit=event=>event.preventDefault();
  filters.oninput=update;
  $("map-toggle").onclick=()=>{
    $("catalog-map").hidden=false;
    $("catalog-list").hidden=true;
    $("map-toggle").setAttribute("aria-pressed","true");
    $("list-toggle").setAttribute("aria-pressed","false");
    update();
    map?.invalidateSize();
  };
  $("list-toggle").onclick=()=>{
    $("catalog-map").hidden=true;
    $("catalog-list").hidden=false;
    $("map-toggle").setAttribute("aria-pressed","false");
    $("list-toggle").setAttribute("aria-pressed","true");
  };
  if(view.params.get("mode")==="map") $("map-toggle").click(); else update();
}
const hasLocation=record=>Number.isFinite(record.latitude)&&Number.isFinite(record.longitude)&&
  Math.abs(record.latitude)<=90&&Math.abs(record.longitude)<=180;

function renderCity(){
  const items=recordsFor("search",view.city);
  root.innerHTML=page(link("Back to Morocco",path("home"),"back")+tags(view.city.name,"City walk")+
    '<div class="tabs">'+Object.entries(modules).map(([key,meta])=>link(meta.label,cityPath(key),"action")).join("")+'</div>'+
    '<div class="layout"><section><div class="map-shell"><div id="leaf-map" class="leaf-map" aria-label="'+label("Street map")+'"></div>'+
    '<div class="avatar" id="avatar" role="img" aria-label="Your character"><i></i><b></b><span></span></div></div>'+
    '<div class="map-controls"><span>'+label("Walk with arrow keys or select a point on the map.")+'</span>'+
    [["left","←"],["up","↑"],["down","↓"],["right","→"]].map(([dir,symbol])=>press(symbol,"walk",'data-direction="'+dir+'" aria-label="Walk '+dir+'"')).join("")+
    '</div></section>'+article("Street map",'<div id="city-results">'+resultItems(items.filter(hasLocation))+'</div>')+'</div>');
  mapRows(items,true);
}
function mapRows(records,walkable=false){
  const target=$("leaf-map");
  if(!window.L){target.textContent="The street map is unavailable.";return;}
  if(!map){
    map=L.map(target,{scrollWheelZoom:false,keyboard:!walkable,zoomControl:true}).setView(view.city.center,walkable?17:13);
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
  const saved=state.favourites.some(item=>item.owner===owner()&&item.id===record.id);
  const actionAttribute='data-id="'+esc(record.id)+'"';
  root.innerHTML=page(link(title,cityPath(view.name),"back")+tags(record.name,record.category)+statusLine()+
    '<section class="panel detail">'+(record.example?'<p class="muted">'+label("Sample listing")+'</p>':"")+details(record)+
    '<div class="actions">'+press(saved?"Remove favourite":"Save favourite","favourite",actionAttribute)+
    (record.module==="sports"?press("Reserve tickets","reserve",actionAttribute,"primary"):"")+
    (record.module==="jobs"?press("Apply","apply",actionAttribute,"primary"):"")+
    (record.module==="business"?press("Send enquiry","enquiry",actionAttribute,"primary"):"")+
    (hasLocation(record)?link("View on map",cityPath(view.name,{mode:"map"}),"action"):"")+'</div></section>');
}

function chooseAction(action,record){
  if(!record)return;
  if(action==="favourite"){
    requireUser(()=>{
      const index=state.favourites.findIndex(item=>item.owner===owner()&&item.id===record.id);
      if(index<0) state.favourites.push({owner:owner(),id:record.id,city:record.city});
      else state.favourites.splice(index,1);
      save();
      notification(index<0?"Favourite saved.":"Favourite removed.");
    });
    return;
  }
  if(action==="reserve") requireUser(()=>reservation(record));
  if(action==="apply") requireUser(()=>application(record));
  if(action==="enquiry") enquiry(record);
}
function reservation(record){
  const schema=[input("quantity","Tickets","number")];
  showDialog("Reserve tickets",'<p>'+esc(record.name)+'</p><p>'+esc(
    [record.venue,record.date,record.time,record.price==null?null:record.price+" MAD per ticket"].filter(Boolean).join(" · ")
  )+'</p>'+simpleForm(schema,{quantity:1},"Review reservation",["quantity"]),form=>{
    const quantity=values(form,schema).quantity;
    if(!Number.isInteger(quantity)||quantity<1){
      formStatus(form,"Enter a positive whole number of tickets.");return;
    }
    const total=quantity*(record.price||0);
    showDialog("Review reservation",'<dl><dt>'+label("Tickets")+'</dt><dd>'+quantity+'</dd><dt>Total</dt><dd>'+
      total.toFixed(2)+' MAD</dd></dl>'+info("Preview data stays in this browser tab.")+
      '<form data-form="dialog"><div class="actions"><button class="primary">'+label("Save preview reservation")+'</button></div></form>',()=>{
        state.records.push({id:id(),module:"bookings",city:record.city,name:record.name,owner:owner(),
          venue:record.venue,date:record.date,quantity,total,status:"Pending"});
        log("Booking created",record.name);
        save();
        notice="Preview reservation saved.";
        navigate(cityPath("account",{tab:"bookings"}));
      });
  });
}
function application(record){
  const available=state.cvs.filter(cv=>cv.owner===owner());
  if(!available.length){
    showDialog("Apply",'<p>Add a CV to your account before trying the application form.</p>'+
      link("New CV",cityPath("account",{tab:"cv",edit:"new",job:record.id}),"action primary"));
    return;
  }
  const schema=[
    input("cvId","CV","select",available.map(cv=>({value:cv.id,label:cv.name}))),
    input("note","Cover note","textarea")
  ];
  showDialog("Apply",'<p>'+esc(record.name)+'</p>'+simpleForm(schema,{},"Save preview application",["cvId"]),form=>{
    const {cvId,note}=values(form,schema);
    const cv=available.find(item=>item.id===cvId);
    if(!cv){formStatus(form,"Choose one of your CVs.");return;}
    state.records.push({id:id(),module:"applications",city:record.city,name:record.name,owner:owner(),
      cv:cv.name,date:today(),note,status:"Received"});
    log("Application submitted",record.name);
    save();
    notice="Preview application saved.";
    navigate(cityPath("account",{tab:"applications"}));
  });
}
function enquiry(record){
  const schema=[input("name","Full name"),input("email","Email","email"),input("subject","Subject"),input("message","Message","textarea")];
  showDialog("Send enquiry",'<p>'+esc(record.name)+'</p>'+simpleForm(schema,
    {email:owner()},"Preview enquiry",["name","email","subject","message"]),form=>{
    form.reset();
    formStatus(form,"Enquiry preview completed. No message was sent.");
  });
}
function personalNav(tab){
  return '<nav class="tabs">'+Object.entries({profile:"Profile",cv:"My CVs",favourites:"Favourites",
    bookings:"My bookings",applications:"My applications"}).map(([key,title])=>
    '<a class="action" href="'+esc(cityPath("account",{tab:key}))+'"'+(tab===key?' aria-current="page"':'')+
    '>'+label(title)+'</a>'
  ).join("")+press("Sign out","sign-out")+'</nav>';
}
function accountNav(){
  return '<nav class="tabs">'+Object.entries({signin:"Sign in",register:"Register",reset:"Reset password"}).map(([key,title])=>
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
function renderAccount(){
  const tab=view.params.get("tab")||(canUse()?"profile":"signin");
  const resetFields=[input("email","Email","email")];
  const credentials=[input("email","Email","email"),input("password","Password","password")];
  const registration=[input("name","Full name"),...credentials,input("confirm","Confirm password","password"),
    input("role","Role","select",["Registered user","Student","Tourist"])];
  let content;
  if(tab==="reset"||tab==="reset-preview"){
    const schema=tab==="reset"?resetFields:[input("password","Password","password"),input("confirm","Confirm password","password")];
    content=accountNav()+accountForm("Reset password",
      info("Preview data stays in this browser tab.")+(tab==="reset-preview"?'<p>Sample reset step. No reset token is validated.</p>':"")+
      pageForm(schema,{},"Reset password",schema.map(field=>field.key),tab));
  } else if(!canUse()||tab==="signin"||tab==="register"){
    const form=tab==="register"?registration:credentials;
    content=accountNav()+accountForm(tab==="register"?"Register":"Sign in",
      info("Use sample details. This preview does not authenticate accounts or save passwords.")+
      pageForm(form,{},tab==="register"?"Register":"Sign in",form.map(field=>field.key),tab==="register"?"register":"signin")+
      '<div class="actions">'+press("Start sample user session","sample-user")+'</div>');
  } else if(tab==="cv"){
    const edit=view.params.get("edit");
    const cvs=state.cvs.filter(item=>item.owner===owner());
    const record=cvs.find(item=>item.id===edit);
    content=personalNav(tab)+(edit && (record || edit==="new")
      ?accountForm(edit==="new"?"New CV":"Edit",info("The selected file stays in memory while this page is open.")+
        pageForm(schemas.cv,record,"Save",record?["name"]:["name","file"],"cv"))
      :'<section class="panel stack"><div class="actions">'+link("New CV",cityPath("account",{tab:"cv",edit:"new"}),"action primary")+'</div>'+
        (cvs.length?cvs.map(cv=>'<article class="record"><div><h3>'+esc(cv.name)+'</h3><p>'+
        esc([cv.filename,cv.skills,cv.updated].filter(Boolean).join(" · "))+'</p></div><div class="actions">'+
        link("Edit",cityPath("account",{tab:"cv",edit:cv.id}),"action")+
        press("Open file","open-cv",'data-id="'+esc(cv.id)+'"')+
        press("Delete","delete-cv",'data-id="'+esc(cv.id)+'"',"danger")+'</div></article>').join(""):
        '<p>'+label("Nothing to display.")+'</p>')+'</section>');
  } else if(tab==="favourites"){
    const saved=state.favourites.filter(item=>item.owner===owner()).map(item=>
      byId(item.id) || (cityById(item.city)?recordsFor("tourism",cityById(item.city)).find(record=>record.id===item.id):null)
    ).filter(record=>record&&record.published!==false);
    content=personalNav(tab)+article("Favourites",resultItems(saved));
  } else if(tab==="bookings"||tab==="applications"){
    const records=state.records.filter(record=>record.module===tab&&record.owner===owner());
    content=personalNav(tab)+article(tab==="bookings"?"My bookings":"My applications",
      table(records,["name","date","status",tab==="bookings"?"quantity":"cv"],record=>{
        const attr='data-id="'+esc(record.id)+'"';
        return press("Details","personal-record",attr)+
          (tab==="bookings"&&record.status!=="Cancelled"?press("Cancel","cancel-booking",attr):"");
      }));
  } else {
    const profile=state.profiles[owner()] || {name:state.user.name,email:owner()};
    const fields=schemas.profile.filter(field=>
      (!["studentNumber","institution"].includes(field.key)||state.user.role==="Student")&&
      (field.key!=="travelPurpose"||state.user.role==="Tourist"));
    content=personalNav("profile")+accountForm("Profile",
      '<p>'+label(state.user.role)+'</p>'+pageForm(fields,profile,"Save",["name","email"],"profile"));
    queueMicrotask(()=>{
      const email=root.querySelector('input[name="email"]');
      if(email) email.readOnly=true;
    });
  }
  root.innerHTML=page(tags("Account")+statusLine()+content);
}
function rememberUser(email,name,role){
  state.user={email:normalize(email),name,role};
  state.guest=false;
  log("Login","Preview session");
  save();
  const target=accountReturn();
  const continuation=pending?.callback;
  pending=undefined;
  notice="Preview session started.";
  navigate(role==="Administrator"?cityPath("admin"):target);
  if(continuation&&role!=="Administrator") continuation();
}
function accountSubmit(form,type){
  const data=Object.fromEntries(new FormData(form));
  if(type==="signin"){
    rememberUser(data.email,data.email.split("@")[0],"Registered user");return;
  }
  if(type==="register"||type==="reset-preview"){
    if(data.password!==data.confirm){formStatus(form,"Passwords must match.");return;}
    if(type==="register"){rememberUser(data.email,data.name,data.role);return;}
    navigate(cityPath("account",{tab:"signin"}));
    notification("Password reset preview completed.");
    return;
  }
  if(type==="reset"){
    form.reset();
    formStatus(form,"Reset request preview completed. No email was sent.");
    if(!form.querySelector("[data-reset-next]")){
      const next=document.createElement("a");
      next.className="action";
      next.dataset.resetNext="";
      next.href=cityPath("account",{tab:"reset-preview",returnTo:view.params.get("returnTo")});
      next.textContent="Open sample reset screen";
      form.append(next);
    }
    return;
  }
  if(!canUse()) return;
  if(type==="profile"){
    state.profiles[owner()]={...data,email:owner()};
    state.user.name=data.name;
    save();notification("Profile preview saved.");return;
  }
  if(type==="cv"){
    const edit=view.params.get("edit");
    const previous=state.cvs.find(cv=>cv.id===edit&&cv.owner===owner());
    const entry=values(form,schemas.cv);
    if(entry.file && !/\.(pdf|doc|docx|odt)$/i.test(entry.file.name)){
      formStatus(form,"Choose a PDF, DOC, DOCX or ODT file.");return;
    }
    if(entry.file?.size>10*1024*1024){
      formStatus(form,"Select a file smaller than 10 MB.");return;
    }
    const key=previous?.id || id();
    if(entry.file) cvFiles.set(key,entry.file);
    const newRecord={id:key,owner:owner(),name:entry.name,description:entry.description,skills:entry.skills,
      keywords:entry.keywords,filename:entry.file?.name||previous?.filename,updated:today()};
    if(previous) Object.assign(previous,newRecord);else state.cvs.push(newRecord);
    save();
    notice="CV preview saved.";
    const job=view.params.get("job");
    const vacancy=job&&byId(job);
    if(vacancy){
      navigate(recordUrl(vacancy));
      application(vacancy);
    } else navigate(cityPath("account",{tab:"cv"}));
  }
}

const tableLabels={name:"Name",email:"Email",role:"Role",enabled:"Enabled",status:"Status",date:"Date",source:"Source",category:"Category",quantity:"Tickets",cv:"CV",owner:"Account",city:"City",venue:"Venue"};
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
  const tab=Object.hasOwn(adminSections,view.params.get("tab"))?view.params.get("tab"):"overview";
  if(!isAdmin()){
    root.innerHTML=page(tags("Administration")+article("Sign in",info("The administrator preview demonstrates the interface with sample records.")+
      '<div class="actions">'+link("Sign in",cityPath("account",{tab:"signin",returnTo:cityPath("admin")}),"action")+
      press("Open administrator preview","admin-preview","","primary")+'</div>'));
    return;
  }
  const menu='<aside class="admin-nav" aria-label="Administration">'+Object.entries(adminSections).map(([id,title])=>
    '<a href="'+esc(cityPath("admin",{tab:id}))+'"'+(id===tab?' aria-current="page"':'')+'>'+label(title)+'</a>'
  ).join("")+press("Sign out","sign-out")+'</aside>';
  let body;
  if(tab==="overview"){
    const metrics=[
      ["Users",state.records.filter(record=>record.module==="users").length],
      ["Bookings",state.records.filter(record=>record.module==="bookings").length],
      ["Applications",state.records.filter(record=>record.module==="applications").length],
      ["Directory records",state.records.filter(record=>Object.hasOwn(modules,record.module)||record.module==="news").length]
    ];
    body=article("Dashboard",'<div class="stats">'+metrics.map(([name,count])=>'<div><strong>'+count+'</strong>'+label(name)+'</div>').join("")+
      '</div><h3>Recent activity</h3>'+table(state.records.filter(record=>record.module==="activity").slice(0,8),["name","source","date","status"]));
  }else{
    const locked=["bookings","applications","activity"].includes(tab);
    body=article(adminSections[tab],'<div class="toolbar">'+field(input("admin-query","Search","search"))+
      (locked?"":press("Add","admin-add",'data-section="'+tab+'"',"primary"))+'</div><div id="admin-table"></div>');
  }
  root.innerHTML=page(tags("Administration")+statusLine()+'<div class="admin-layout">'+menu+'<div>'+body+'</div></div>');
  if(tab==="overview") return;
  const search=root.querySelector('[name="admin-query"]');
  const columns=tab==="users"?["name","email","role","enabled"]:
    tab==="activity"?["name","source","date","status"]:
    ["bookings","applications"].includes(tab)?["name","owner","date","status"]:
    tab==="slots"?["name","venue","status"]:["name","category","city"];
  const update=()=>{
    const records=state.records.filter(record=>record.module===tab&&searchMatch(search.value,record));
    $("admin-table").innerHTML=table(records,columns,record=>{
      const attr='data-id="'+esc(record.id)+'" data-section="'+tab+'"';
      return press("Details","admin-details",attr)+
        (tab==="activity"?"":press("Edit","admin-edit",attr))+
        (tab==="users"?press(record.enabled?"Disable":"Enable","admin-toggle",attr):"")+
        (!["activity","bookings","applications"].includes(tab)?press("Delete","admin-delete",attr,"danger"):"");
    });
  };
  search.oninput=update;update();
}
function adminEdit(section,record){
  if(!isAdmin()) return;
  const isStatus=["bookings","applications"].includes(section);
  const schema=isStatus?schemas[section].filter(field=>field.key==="status"):[...schemas[section]];
  if(!["users","cities"].includes(section)&&!isStatus){
    schema.unshift(input("city","City","select",cities.map(city=>({value:city.id,label:city.name}))));
  }
  const initial=record || {city:view.city.id,published:true,enabled:true};
  showDialog(record?"Edit":"Add",simpleForm(schema,initial,"Save",isStatus?["status"]:["name"]),form=>{
    const updates=values(form,schema);
    if((updates.latitude!=null&&Math.abs(updates.latitude)>90)||(updates.longitude!=null&&Math.abs(updates.longitude)>180)){
      formStatus(form,"Enter valid latitude and longitude.");return;
    }
    if(record) Object.assign(record,updates);
    else state.records.push({id:id(),module:section,city:view.city.id,...updates});
    log(record?"Record updated":"Record created",updates.name||record.name);
    save();
    dialog.close();
    notification("Preview change saved.");
  });
}
function renderLegal(kind){
  const privacy=kind==="privacy";
  root.innerHTML=page(tags(privacy?"Privacy Policy":"Terms of Service")+article(privacy?"Privacy Policy":"Terms of Service",privacy?
    '<p>This frontend preview stores sample profiles and activity in the current browser tab. It stores the selected language in this browser.</p>'+
    '<p>CV file contents are kept in memory while this page is open. Password fields are used for form demonstration. Street tiles are provided by OpenStreetMap.</p>'+
    '<div class="actions">'+press("Clear preview data","clear-preview","","danger")+'</div>':
    '<p>Use sample information when exploring the atlas. Listings and reservation or application screens demonstrate the guide’s interface.</p>'+
    '<p>Confirm opening hours, service availability and event information with the relevant provider before making plans.</p>'));
}

const actionHandlers={
  enter:button=>enterCity(cityById(button.dataset.city)||preview),
  "show-list":()=>{}, "show-map":()=>{},
  guest:()=>{
    state.guest=true;save();
    const target=pending?.returnTo || cityPath("city");
    pending=undefined;navigate(target);
  },
  walk:button=>walk(button.dataset.direction),
  favourite:button=>chooseAction("favourite",getRecord(button.dataset.id)),
  reserve:button=>chooseAction("reserve",getRecord(button.dataset.id)),
  apply:button=>chooseAction("apply",getRecord(button.dataset.id)),
  enquiry:button=>chooseAction("enquiry",getRecord(button.dataset.id)),
  "sample-user":()=>rememberUser("traveller@example.test","Sample registered user","Registered user"),
  "admin-preview":()=>rememberUser("admin@example.test","Sample administrator","Administrator"),
  "sign-out":()=>{
    log("Logout");state.user=null;state.guest=false;pending=undefined;save();navigate(path("home"));
  },
  "open-cv":button=>{
    const cv=state.cvs.find(item=>item.id===button.dataset.id&&item.owner===owner());
    if(!cv) return;
    const file=cvFiles.get(cv.id);
    if(!file){
      showDialog("Open file",'<p>The file is no longer in memory. Open Edit to choose it again.</p>');
      return;
    }
    const url=URL.createObjectURL(file);
    const download=document.createElement("a");
    download.href=url;download.download=file.name;download.click();
    setTimeout(()=>URL.revokeObjectURL(url),1500);
  },
  "delete-cv":button=>{
    const cv=state.cvs.find(item=>item.id===button.dataset.id&&item.owner===owner());
    if(!cv)return;
    confirmDialog("Delete CV?",cv.name,()=>{
      state.cvs=state.cvs.filter(item=>item.id!==cv.id);cvFiles.delete(cv.id);save();notification("CV removed.");
    });
  },
  "personal-record":button=>{
    const record=byId(button.dataset.id);
    if(record?.owner===owner())showDialog(record.name,details(record));
  },
  "cancel-booking":button=>{
    const record=byId(button.dataset.id);
    if(record?.owner!==owner()||record.module!=="bookings")return;
    showDialog("Cancel reservation?",'<p>'+esc(record.name)+'</p><form data-form="dialog"><button class="danger">Cancel reservation</button></form>',()=>{
      record.status="Cancelled";log("Booking cancelled",record.name);save();dialog.close();notification("Preview reservation cancelled.");
    });
  },
  "admin-add":button=>adminEdit(button.dataset.section),
  "admin-edit":button=>adminEdit(button.dataset.section,byId(button.dataset.id)),
  "admin-details":button=>{
    if(!isAdmin())return;
    const record=byId(button.dataset.id);
    if(record)showDialog(record.name,details(record));
  },
  "admin-toggle":button=>{
    const record=byId(button.dataset.id);
    if(!isAdmin()||record?.module!=="users")return;
    record.enabled=!record.enabled;log(record.enabled?"User enabled":"User disabled",record.name);save();render();
  },
  "admin-delete":button=>{
    const record=byId(button.dataset.id);
    if(!isAdmin()||!record)return;
    confirmDialog("Delete record?",record.name,()=>{
      state.records=state.records.filter(item=>item.id!==record.id);
      log("Record deleted",record.name);save();notification("Preview record removed.");
    });
  },
  "clear-preview":()=>confirmDialog("Clear local preview data?","Account previews, CV metadata and sample changes will be cleared.",()=>{
    state=structuredClone(defaultState);cvFiles.clear();pending=undefined;sessionStorage.removeItem(storageKey);notification("Preview data cleared.");
  })
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
  const name=url.pathname.split("/").at(-1).replace(/\.html$/,"");
  if(Object.hasOwn(modules,name)&&!canUse()&&!state.guest){
    accessGate(url.pathname+url.search,undefined,true);
    return;
  }
  navigate(url.href);
});
document.addEventListener("submit",event=>{
  const type=event.target.dataset.form;
  if(!type)return;
  event.preventDefault();
  if(type==="dialog"){formHandler?.(event.target);return;}
  if(type==="global-search"){
    navigate(path("search",{city:preview.id,q:new FormData(event.target).get("query")}));
    return;
  }
  accountSubmit(event.target,type);
});
document.addEventListener("keydown",event=>{
  const key={ArrowLeft:"left",ArrowRight:"right",ArrowUp:"up",ArrowDown:"down"}[event.key];
  if(!key||view.name!=="city"||dialog.open||event.target.closest?.("input,select,textarea,button,[contenteditable]"))return;
  event.preventDefault();walk(key);
});
window.addEventListener("popstate",()=>{dialog.close();notice="";render(true);});
change(lang());
