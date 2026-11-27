import {esc,t} from "./ui.js";

export function pageOf(rows,page,size){
  const last=Math.max(1,Math.ceil(rows.length/size));
  const current=Math.min(last,Math.max(1,page));
  const start=(current-1)*size;
  return {page:current,rows:rows.slice(start,start+size),hasNext:current<last};
}

export function mountPagination(container,{name,key,renderRows,load}){
  container.innerHTML='<div data-page-rows></div><nav class="pagination" aria-label="'+esc(name)+'">'+
    '<label>'+esc(t("Per page"))+' <select><option value="15">15</option><option value="30">30</option></select></label>'+
    '<button type="button" data-page-back>'+esc(t("Previous"))+'</button>'+
    '<span role="status" data-page-label></span><button type="button" data-page-next>'+esc(t("Next"))+'</button></nav>';
  const body=container.querySelector("[data-page-rows]");
  const select=container.querySelector("select");
  const previous=container.querySelector("[data-page-back]");
  const next=container.querySelector("[data-page-next]");
  const label=container.querySelector("[data-page-label]");
  const storageKey="atlas-page-size-"+key;
  let size=15;
  try{if(sessionStorage.getItem(storageKey)==="30")size=30;}catch{}
  select.value=String(size);
  let page=1;
  let rows=[];
  let signature;
  let revision=0;
  const update=async ()=>{
    const current=++revision;
    let data;
    if(load){
      previous.disabled=next.disabled=true;
      label.textContent=t("Loading…");
      body.textContent="";
      try{
        const items=await load((page-1)*size,size+1,signature);
        if(current!==revision||!container.isConnected)return;
        data={page,rows:items.slice(0,size),hasNext:items.length>size};
      }catch(error){
        if(current!==revision||!container.isConnected)return;
        body.textContent=error.message;
        label.textContent="";
        previous.disabled=page===1;
        return;
      }
    }else data=pageOf(rows,page,size);
    page=data.page;
    body.replaceChildren();
    const content=renderRows(data.rows);
    if(typeof content==="string")body.innerHTML=content;
    else body.append(content);
    label.textContent=t("Page")+" "+page;
    previous.disabled=page===1;
    next.disabled=!data.hasNext;
  };
  select.onchange=()=>{
    size=select.value==="30"?30:15;
    try{sessionStorage.setItem(storageKey,String(size));}catch{}
    page=1;
    update();
  };
  previous.onclick=()=>{if(page>1){page--;update();}};
  next.onclick=()=>{if(!next.disabled){page++;update();}};
  return {
    set(items,filter){
      if(signature!==filter){signature=filter;page=1;}
      rows=items;
      return update();
    }
  };
}
