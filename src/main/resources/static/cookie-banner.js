const consentKey="atlas-cookie-choice";
const languageKey="atlas-language";
const copy={
  en:["Cookie preferences","This site uses browser storage for your session. You can also save your language for future visits.","Only essentials","Remember language","Privacy Policy"],
  fr:["Préférences de cookies","Le site utilise le stockage du navigateur pour votre session. Vous pouvez aussi mémoriser votre langue pour vos prochaines visites.","L’essentiel seulement","Mémoriser la langue","Confidentialité"],
  ar:["تفضيلات ملفات تعريف الارتباط","يستخدم الموقع مساحة تخزين المتصفح لحفظ جلستك. يمكنك أيضًا حفظ لغتك للزيارات المقبلة.","الأساسية فقط","تذكّر اللغة","سياسة الخصوصية"]
};
const motions=matchMedia("(prefers-reduced-motion: reduce)");
let banner;
let frame;
let origin;
let opener;

function get(storage,key){
  try{return storage.getItem(key);}catch{return null;}
}
function put(storage,key,value){
  try{if(value==null)storage.removeItem(key);else storage.setItem(key,value);}catch{}
}
export function readLanguage(){
  return (get(localStorage,consentKey)==="language"&&get(localStorage,languageKey))||
    get(sessionStorage,languageKey)||get(localStorage,languageKey)||"en";
}
export function rememberLanguage(language){
  put(sessionStorage,languageKey,language);
  put(localStorage,languageKey,get(localStorage,consentKey)==="language"?language:null);
}
export function cookieLabel(language){return (copy[language]||copy.en)[0];}

function rect(ctx,x,y,w,h,color){
  ctx.fillStyle=color;
  ctx.fillRect(x,y,w,h);
}

function cat(ctx,x,ground,direction,pose,frame){
  ctx.save();
  ctx.translate(Math.round(x),Math.round(ground));
  ctx.scale(direction,1);
  const outline="#703b27", orange="#e98a40", stripe="#a94c29", cream="#ffe3b8", pink="#bf5763";
  const bodyY=pose==="crouch"?-10:pose==="jump"?-17:-14;

  // Pixel steps in the tail alternate with the stride.
  const tail=frame%2?-17:-15;
  rect(ctx,-15,tail,4,3,outline);
  rect(ctx,-13,tail+2,5,3,orange);
  rect(ctx,-10,bodyY+4,5,3,outline);
  rect(ctx,-9,bodyY+3,15,9,outline);
  rect(ctx,-8,bodyY+4,13,7,orange);
  rect(ctx,-5,bodyY+4,2,3,stripe);
  rect(ctx,0,bodyY+4,2,3,stripe);

  // Paired front and rear legs use distinct frames for the run and jump.
  const legs=pose==="jump"?[[-7,-9,3,3],[-5,-7,4,2],[2,-9,4,3],[5,-7,4,2]]:
    pose==="crouch"?[[-8,-4,4,3],[-6,-2,4,2],[1,-4,4,3],[3,-2,4,2]]:
    [
      [[-9,-6,3,5],[-7,-2,4,2],[2,-6,3,4],[4,-3,4,2]],
      [[-6,-6,3,4],[-4,-3,4,2],[4,-6,3,5],[6,-2,4,2]],
      [[-7,-6,4,4],[-9,-3,4,2],[0,-6,4,4],[-1,-2,4,2]],
      [[-4,-6,3,5],[-4,-2,4,2],[3,-6,3,4],[2,-3,4,2]]
    ][frame];
  legs.forEach(([px,py,w,h],index)=>rect(ctx,px,py,w,h,index%2?cream:outline));

  const headY=bodyY-4;
  rect(ctx,3,headY,10,9,outline);
  rect(ctx,4,headY+1,8,8,orange);
  rect(ctx,4,headY-3,3,4,outline);
  rect(ctx,9,headY-3,3,4,outline);
  rect(ctx,5,headY-2,1,2,pink);
  rect(ctx,10,headY-2,1,2,pink);
  rect(ctx,9,headY+3,2,2,outline);
  rect(ctx,9,headY+5,5,3,cream);
  rect(ctx,13,headY+5,1,1,stripe);
  rect(ctx,6,headY+6,2,1,pink);
  // Small rose ribbon at the collar.
  rect(ctx,2,headY+8,2,3,pink);
  rect(ctx,5,headY+8,2,3,pink);
  rect(ctx,4,headY+9,1,1,"#ffbcc6");
  ctx.restore();
}

function cookie(ctx,x,y,phase){
  ctx.save();
  ctx.translate(Math.round(x),Math.round(y));
  const edge="#71432c", dough="#e6a45e";
  rect(ctx,-3,-5,6,10,edge);
  rect(ctx,-5,-3,10,6,edge);
  rect(ctx,-4,-3,8,6,dough);
  rect(ctx,-3,-4,6,8,dough);
  rect(ctx,-3,-3,3,1,"#f8cf89");
  const chips=phase%2?[[-2,-2],[1,0],[-1,2]]:[[-1,-3],[2,-1],[-3,1]];
  chips.forEach(([a,b])=>rect(ctx,a,b,2,2,edge));
  ctx.restore();
}

function draw(now=0){
  const canvas=banner.querySelector("canvas");
  const ctx=canvas.getContext("2d");
  if(!ctx)return;
  const width=Math.max(136,Math.floor((canvas.clientWidth||360)/2));
  if(canvas.width!==width)canvas.width=width;
  if(origin===undefined)origin=now;
  const seconds=(now-origin)/1000;
  const run=2.6, turn=1.05, half=run+turn;
  const leg=Math.floor(seconds/half);
  const direction=leg%2?-1:1;
  const local=seconds%half;
  const left=11, right=width-11, gap=30;
  const reflect=x=>direction===1?x:width-x;
  let cookieX, catX, cookieHop=0, hop=0;
  let facing=direction, pose="run", gait=Math.floor(seconds*12)%4;
  if(local<run){
    cookieX=left+(right-left-gap*2)*(local/run)+gap*2;
    catX=cookieX-gap;
  }else{
    const turnPhase=(local-run)/turn;
    catX=right-gap;
    cookieX=right-2*gap*turnPhase;
    cookieHop=35*Math.sin(Math.PI*turnPhase);
    const jump=(turnPhase-.21)/.6;
    if(jump>0&&jump<1){
      pose="jump";
      hop=9*Math.sin(Math.PI*jump);
    }else pose="crouch";
    if(turnPhase>.72)facing=-direction;
  }
  if(motions.matches){
    cookieX=width*.68;catX=width*.34;cookieHop=0;hop=0;facing=1;pose="crouch";gait=0;
  }
  ctx.clearRect(0,0,width,canvas.height);
  rect(ctx,0,canvas.height-3,width,1,"#c28a64");
  cookie(ctx,motions.matches?cookieX:reflect(cookieX),canvas.height-9-cookieHop,gait);
  cat(ctx,motions.matches?catX:reflect(catX),canvas.height-4-hop,facing,pose,gait);
  if(!motions.matches&&!banner.hidden)frame=requestAnimationFrame(draw);
}

function stop(){
  if(frame!=null)cancelAnimationFrame(frame);
  frame=undefined;
  origin=undefined;
}

function makeBanner(){
  const node=document.createElement("aside");
  node.id="cookie-banner";
  node.setAttribute("aria-labelledby","cookie-heading");
  node.hidden=true;
  node.innerHTML='<div class="cookie-copy"><h2 id="cookie-heading"></h2><p id="cookie-description"></p>'+
    '<div class="cookie-actions"><button type="button" data-choice="essential"></button>'+
    '<button type="button" data-choice="language"></button><a href="privacy.html"></a></div></div>'+
    '<div class="cookie-track" aria-hidden="true"><canvas width="180" height="54"></canvas></div>';
  node.querySelectorAll("button[data-choice]").forEach(button=>button.onclick=()=>{
    const language=document.documentElement.lang||readLanguage();
    put(localStorage,consentKey,button.dataset.choice);
    rememberLanguage(language);
    node.hidden=true;
    stop();
    opener?.focus();
    opener=undefined;
  });
  document.body.append(node);
  if(window.ResizeObserver) {
    new ResizeObserver(()=>{
      if(banner&&!banner.hidden&&motions.matches)draw(performance.now());
    }).observe(node);
  }
  return node;
}

export function updateCookieBanner(language,trigger){
  if(!banner)banner=makeBanner();
  const [title,description,essential,optional,privacy]=copy[language]||copy.en;
  banner.querySelector("h2").textContent=title;
  banner.querySelector("p").textContent=description;
  banner.querySelector('[data-choice="essential"]').textContent=essential;
  banner.querySelector('[data-choice="language"]').textContent=optional;
  banner.querySelector("a").textContent=privacy;
  if(trigger||!get(localStorage,consentKey)){
    const wasHidden=banner.hidden;
    banner.hidden=false;
    if(trigger){
      opener=trigger;
      banner.querySelector("button").focus();
    }
    if(wasHidden||frame==null){stop();draw(performance.now());}
  }
}

motions.addEventListener?.("change",()=>{
  if(banner&&!banner.hidden){stop();draw(performance.now());}
});
