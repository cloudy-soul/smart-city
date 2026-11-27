import {mountPagination} from "./pagination.js";
import {preferences} from "./cookie-banner.js";
import {request} from "./catalog-api.js";
import {esc} from "./ui.js";

const pages=new Set(["home","city","search","tourism","education","sports","jobs","business","privacy","terms"]);
let lastAddress;
let collection;
const pending=new Set();

document.addEventListener("atlas-consent-change",()=>{
  if(!preferences()?.analytics)for(const controller of pending)controller.abort();
});

export async function recordNavigation(page){
  const address=location.pathname+location.search;
  if(address===lastAddress)return;
  lastAddress=address;
  if(!pages.has(page)||!preferences()?.analytics)return;
  const controller=new AbortController();
  pending.add(controller);
  try{
    collection??=fetch("/api/atlas-visits/collection",{credentials:"omit",referrerPolicy:"no-referrer"})
      .then(response=>response.ok?response.json():{enabled:false}).catch(()=>({enabled:false}));
    if(!(await collection).enabled||controller.signal.aborted||!preferences()?.analytics)return;
    await fetch("/api/atlas-visits",{
      method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify({page:page.toUpperCase()}),
      signal:controller.signal,referrerPolicy:"no-referrer"
    });
  }catch{
    // Optional counting must never interrupt navigation.
  }finally{
    pending.delete(controller);
  }
}

export const analyticsMarkup='<section class="panel stack" id="atlas-analytics">'+
  '<h2>Analytics</h2><p>Page views from opted-in visits. Dates use UTC.</p>'+
  '<form class="filters" aria-label="Filter analytics">'+
  '<label>Start date <input name="from" type="date"></label>'+
  '<label>End date <input name="to" type="date"></label>'+
  '<label>Group locations by <select name="location"><option value="COUNTRY">Country</option>'+
  '<option value="REGION">Region</option><option value="CITY">City</option></select></label>'+
  '<button type="submit">Apply filters</button></form>'+
  '<p class="analytics-status" role="status"></p><div class="analytics-results"></div></section>';

const moduleLabels={TOURISM:"Tourism",EDUCATION:"Education",SPORTS:"Sport events",JOBS:"Jobs",BUSINESS:"Business"};

function counts(heading,values,format,max){
  if(!values.length)return "<p>No views to show.</p>";
  return '<table><thead><tr><th scope="col">'+heading+
    '</th><th scope="col">Page views</th></tr></thead><tbody>'+values.map(row=>'<tr><th scope="row">'+
    esc(format(row.label))+'</th><td>'+row.views+
    (max?'<meter aria-label="Page views on '+esc(row.label)+'" min="0" max="'+max+'" value="'+row.views+'"></meter>':"")+
    '</td></tr>').join("")+'</tbody></table>';
}

export function mountAnalytics(container){
  const form=container.querySelector("form");
  const status=container.querySelector(".analytics-status");
  const results=container.querySelector(".analytics-results");
  let revision=0;
  const load=async ()=>{
    const current=++revision;
    const filters=new URLSearchParams(new FormData(form));
    for(const name of ["from","to"])if(!filters.get(name))filters.delete(name);
    status.textContent="Loading analytics…";
    results.innerHTML="";
    try{
      const data=await request("atlas-visits/report?"+filters);
      if(current!==revision||!container.isConnected)return;
      form.elements.from.value=data.from;
      form.elements.to.value=data.to;
      status.textContent=data.total?"":"No visits in this date range.";
      const series=[
        {title:"Visits by day",heading:"Date",rows:data.days,key:"days",max:data.days.reduce((max,row)=>Math.max(max,row.views),0)},
        {title:"Most visited modules",heading:"Module",rows:data.modules,key:"modules",format:value=>moduleLabels[value]||value},
        {title:"Approximate visitor locations",heading:"Location",rows:data.locations,key:"locations"}
      ];
      results.innerHTML='<p class="analytics-total"><strong>'+data.total+'</strong> total page views</p>'+
        (data.total?'<div class="analytics-grid">'+series.map(({title,key})=>
          '<section><h3>'+title+'</h3><div data-series="'+key+'"></div></section>').join("")+'</div>':"");
      if(data.total){
        for(const {title,heading,rows,key,max,format=value=>value} of series){
          mountPagination(results.querySelector('[data-series="'+key+'"]'),{name:title,key:"analytics-"+key,
            renderRows:items=>counts(heading,items,format,max)}).set(rows);
        }
      }
    }catch(error){
      if(current===revision&&container.isConnected)status.textContent=error.message;
    }
  };
  form.onsubmit=event=>{event.preventDefault();load();};
  load();
}
