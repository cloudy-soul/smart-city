import {VectorTile,Pbf} from "./assets/vector-tile/decoder.js";

const metadataUrl="https://tiles.openfreemap.org/planet";
const tileZoom=14;
let tileTemplate;
const classes = [
  ["education","University",/^(university)$/],
  ["education","College",/^(college)$/],
  ["education","School",/^(school|kindergarten)$/],
  ["education","Library",/^library$/],
  ["tourism","Restaurant",/^(restaurant|cafe|fast_food|food_court|ice_cream)$/],
  ["tourism","Culture",/^(museum|gallery|theatre|arts_centre|cinema)$/],
  ["tourism","Landmark",/^(attraction|monument|memorial|castle|viewpoint|zoo|theme_park)$/],
  ["tourism","Accommodation",/^(hotel|hostel|motel|guest_house|camp_site)$/],
  ["tourism","ATM",/^atm$/],
  ["tourism","Healthcare",/^(hospital|clinic|doctors|dentist|pharmacy)$/],
  ["tourism","Emergency services",/^(police|fire_station)$/],
  ["tourism","Transport",/^(bus_station|railway_station|station|ferry_terminal)$/],
  ["tourism","Rental",/^(car_rental|bicycle_rental)$/],
  ["tourism","Air travel",/^(aerodrome|airport)$/],
  ["tourism","Shopping",/^(mall|supermarket|department_store|marketplace|convenience)$/],
  ["sports","Other sport",/^(stadium|sports_centre|pitch|fitness_centre|track)$/],
  ["sports","Swimming",/^swimming_pool$/],
  ["business","Company",/^(shop|office|commercial|craft|bank)$/]
];
export const liveCategories = module => [...new Set(classes.filter(row=>!module||row[0]===module).map(row=>row[1]))];

function classify(properties){
  return classes.find(([, ,match])=>match.test(properties.subclass||"")) ||
    classes.find(([, ,match])=>match.test(properties.class||""));
}

function tilesFor(bounds){
  const size=2**tileZoom;
  const x=lon=>Math.floor((lon+180)/360*size);
  const y=lat=>{
    const radians=Math.max(-85.05,Math.min(85.05,lat))*Math.PI/180;
    return Math.floor((1-Math.asinh(Math.tan(radians))/Math.PI)/2*size);
  };
  const left=x(bounds.getWest()), right=x(bounds.getEast());
  const top=y(bounds.getNorth()), bottom=y(bounds.getSouth());
  const count=(right-left+1)*(bottom-top+1);
  if(count<1||count>16||left<0||right>=size)return [];
  const tiles=[];
  for(let col=left;col<=right;col++){
    for(let row=top;row<=bottom;row++)tiles.push([col,row]);
  }
  return tiles;
}

async function request(url,signal){
  const response=await fetch(url,{signal,cache:"no-store"});
  if(!response.ok)throw new Error("OpenFreeMap request failed");
  return response;
}

export async function loadViewportPlaces(bounds,signal){
  const tiles=tilesFor(bounds);
  if(!tiles.length)return {tooWide:true,places:[]};
  if(!tileTemplate){
    const metadata=await (await request(metadataUrl,signal)).json();
    const template=metadata.tiles?.[0];
    if(!template?.startsWith("https://tiles.openfreemap.org/"))throw new Error("Missing tile source");
    tileTemplate=template;
  }
  const batches=await Promise.all(tiles.map(async ([x,y])=>{
    const url=tileTemplate.replace("{z}",tileZoom).replace("{x}",x).replace("{y}",y);
    const bytes=await (await request(url,signal)).arrayBuffer();
    const layer=new VectorTile(new Pbf(new Uint8Array(bytes))).layers.poi;
    const places=[];
    for(let i=0;layer&&i<layer.length;i++){
      const feature=layer.feature(i);
      const tags=feature.properties;
      const name=tags.name||tags["name:latin"]||tags["name:nonlatin"]||tags.name_en;
      const rule=classify(tags);
      if(feature.type!==1||!name||!rule)continue;
      const geometry=feature.toGeoJSON(x,y,tileZoom).geometry;
      if(geometry.type!=="Point")continue;
      const [longitude,latitude]=geometry.coordinates;
      if(!bounds.contains([latitude,longitude]))continue;
      const [module,category]=rule;
      places.push({name,module,category,latitude,longitude,kind:tags.subclass||tags.class,live:true});
    }
    return places;
  }));
  const unique=new Map();
  for(const place of batches.flat()){
    const key=[place.name.toLowerCase(),place.category,place.latitude.toFixed(5),place.longitude.toFixed(5)].join("|");
    unique.set(key,place);
  }
  return {tooWide:false,places:[...unique.values()].sort((a,b)=>a.name.localeCompare(b.name))};
}
