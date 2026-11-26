// Small raster illustrations are drawn directly in the browser.
const scenes = {
  casablanca:"mosque",rabat:"tower",marrakech:"minaret",fes:"gate",tangier:"fort",
  tetouan:"medina",el_hoceima:"coast",nador:"lagoon",oujda:"medina",larache:"ruins",
  beni_mellal:"spring",errachidia:"gorge",asfi:"pottery",essaouira:"fort",agadir:"beach",
  tiznit:"silver",tata:"oasis",sidi_ifni:"arch",guelmim:"market",tantan:"market",
  laayoune:"church",boujdour:"lighthouse",dakhla:"lagoon",awsard:"dunes",lagouira:"coast"
};
export function paint(canvas,city){
  const c=canvas.getContext("2d");
  if(!c) return;
  const dark="#4a4437", green="#425f48", red="#8d4e36", sand="#ad8d60", pale="#d6bd84", water="#466f71";
  const rect=(x,y,w,h,color)=>{c.fillStyle=color;c.fillRect(x,y,w,h);};
  const blocks=(list,color)=>list.forEach(([x,y,w,h])=>rect(x,y,w,h,color));
  const tower=(x,y,w,h,stone)=>{rect(x,y,w,h,stone);rect(x+w-3,y,3,h,dark);rect(x-2,y,w+4,3,green);rect(x+3,y+8,3,6,dark);};
  const arch=(x,y,w,h,color)=>blocks([[x+3,y,w-6,3],[x+1,y+3,w-2,3],[x,y+6,w,h-6]],color);
  const sea=y=>{rect(0,y,96,58-y,water);blocks([[8,y+4,15,1],[36,y+8,13,1],[68,y+5,17,1]],"#a9b8a0");};
  const palm=(x,y)=>{rect(x,y+4,2,15,dark);blocks([[x-7,y+3,15,2],[x-4,y,10,3],[x-2,y-2,5,3]],green);};
  const dunes=()=>blocks([[0,38,96,20],[9,34,31,5],[53,30,33,9],[62,27,15,3]],sand);
  rect(0,0,96,58,"#94a38b");rect(0,36,96,22,pale);
  blocks([[76,7,7,11],[74,9,11,7],[7,14,16,2],[33,11,11,2]],"#e9c991");
  rect(0,48,96,10,sand);
  const kind=scenes[city.id];
  switch(kind){
    case "mosque":
      sea(46);rect(16,32,51,15,pale);blocks([[19,27,44,5],[25,24,31,3]],green);
      tower(64,8,12,39,pale);rect(68,5,3,4,dark);for(let x=23;x<63;x+=9)rect(x,36,4,11,dark);break;
    case "tower":
      tower(40,12,17,37,red);
      for(const [x,y] of [[12,34],[22,39],[32,36],[63,33],[73,37],[81,40]]){rect(x,y,5,2,pale);rect(x+1,y+2,3,48-y,red);}break;
    case "minaret":
      rect(19,32,56,16,pale);tower(44,8,13,41,sand);rect(49,3,2,6,green);
      palm(14,32);palm(79,34);break;
    case "gate":
      rect(16,25,64,24,pale);blocks([[15,22,66,5],[20,18,56,4]],"#39586c");
      for(const [x,y,w] of [[22,34,12],[37,29,22],[62,34,12]]){arch(x,y,w,49-y,"#39586c");rect(x+3,y+7,w-6,42-y,dark);}break;
    case "fort":
      sea(44);blocks([[10,34,76,15],[14,27,14,9],[63,24,15,12]],pale);
      for(let x=12;x<84;x+=9)rect(x,31,4,5,pale);arch(42,39,11,10,dark);break;
    case "church":
      rect(23,31,50,18,pale);tower(23,17,10,32,pale);tower(63,17,10,32,pale);arch(42,34,12,15,dark);
      blocks([[27,12,2,6],[25,14,6,1],[67,12,2,6],[65,14,6,1]],dark);break;
    case "lighthouse":
      sea(42);rect(29,44,37,7,sand);tower(43,15,10,33,pale);
      blocks([[44,11,8,4],[45,27,6,3],[45,36,6,3]],red);break;
    case "ruins":
      blocks([[10,45,77,4],[13,41,24,5],[62,38,21,9]],sand);
      for(const x of [17,29,46,60]){rect(x,25+x%5,4,20-x%5,pale);rect(x-1,24+x%5,6,2,sand);}break;
    case "pottery": case "silver":
      rect(12,40,72,10,sand);
      for(const [x,y] of [[19,29],[34,33],[49,24],[65,28]]){blocks([[x+2,y,8,3],[x,y+3,12,9],[x+2,y+12,8,3]],kind==="silver"?"#a4aea0":red);rect(x+2,y+6,8,2,dark);}break;
    case "market":
      dunes();
      for(const [x,y] of [[12,31],[39,27],[65,33]]){blocks([[x+7,y,6,3],[x+3,y+3,14,5],[x,y+8,21,11]],pale);rect(x+8,y+10,5,9,red);}break;
    case "gorge":
      dunes();blocks([[0,20,24,24],[21,28,15,15],[63,23,18,19],[77,16,19,30]],red);
      blocks([[39,39,17,4],[34,43,18,4],[27,47,22,5],[21,52,25,6]],water);break;
    case "spring": case "oasis":
      dunes();rect(18,44,66,10,water);palm(20,30);palm(68,28);palm(81,34);
      if(kind==="spring")blocks([[34,32,26,11],[39,28,16,5]],pale);break;
    case "dunes": dunes();blocks([[8,39,20,3],[32,44,31,3],[67,35,17,2]],red);break;
    case "arch":
      sea(41);blocks([[8,30,37,17],[15,24,48,8],[24,20,27,5],[59,28,15,18]],red);arch(46,32,12,15,water);break;
    case "lagoon":
      sea(33);blocks([[0,38,44,20],[32,42,31,16],[58,47,24,11],[78,51,18,7]],pale);break;
    case "coast": case "beach":
      sea(40);blocks([[0,28,35,12],[0,23,19,6],[11,34,58,7]],kind==="beach"?pale:green);
      if(kind==="beach")palm(20,24);break;
    default:
      for(const [x,y,w] of [[9,34,16],[22,26,16],[35,31,23],[54,24,16],[69,33,17]]){rect(x,y,w,49-y,pale);rect(x,y,w,2,"#eee1b8");rect(x+4,y+5,4,7,dark);}
      arch(37,35,11,14,red);
  }
}
