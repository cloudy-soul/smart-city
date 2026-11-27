export async function request(endpoint, method="GET", payload){
  const multipart=payload instanceof FormData;
  const response=await fetch("/api/"+endpoint,{
    method,
    headers:payload&&!multipart?{"Content-Type":"application/json"}:{},
    body:multipart?payload:payload?JSON.stringify(payload):undefined
  });
  if(!response.ok){
    if(response.status===404&&(endpoint==="account/register"||endpoint==="account/signin")){
      throw new Error("The account endpoint is unavailable. Open Atlas from the current Spring Boot server. If you already have it open, restart Spring Boot and reload this page.");
    }
    if(method==="PUT"&&[404,405].includes(response.status)&&(endpoint==="account"||endpoint==="account/password")){
      throw new Error("This Spring Boot server does not support saving account changes yet. Restart it from the current project and reload this page.");
    }
    const problem=await response.json().catch(()=>({}));
    throw new Error(problem.detail||problem.message||"Request failed ("+response.status+").");
  }
  return response.status===204?null:response.json();
}

export const collections={
  cities:"cities",
  tourism:"tourism_places",
  education:"educational_institutions",
  venues:"venues",
  business:"companies",
  slots:"venue_slots",
  sports:"sport_events",
  jobs:"jobs",
  news:"business_news",
  bookings:"bookings",
  applications:"job_applications",
  activity:"audit_history"
};
const key=(section,id)=>id==null?null:section+"-"+id;
const dbId=id=>id?Number(id.slice(id.lastIndexOf("-")+1)):null;

export function catalogRecord(section,row,records){
  const base={...row,id:key(section,row.id),module:section,apiId:row.id};
  if(section==="cities"){
    return {...base,city:row.name.toLowerCase().replace(/[^a-z0-9]+/g,"-").replace(/^-|-$/g,"")};
  }
  const find=(module,id)=>records.find(item=>item.id===key(module,id));
  const city=find("cities",row.cityId);
  const venue=find("venues",row.venueId);
  const company=find("business",row.companyId);
  const slot=find("slots",row.slotId);
  const location={city:city?.city,cityRef:city?.id,area:row.address};
  switch(section){
    case "tourism": return {...base,...location,hours:row.openingHours};
    case "education": return {...base,...location,category:row.institutionType,email:row.contactEmail};
    case "venues": return {...base,...location,category:row.venueType};
    case "business": return {...base,cityName:row.city,industry:row.title,city:records.find(item=>item.module==="cities"&&item.name===row.city)?.city
      || row.city.toLowerCase().trim().replace(/[^a-z0-9]+/g,"-"),category:row.title,area:row.address};
    case "slots": return {...base,name:row.startsAt.replace("T"," "),venue:venue?.name,city:venue?.city,
      venueRef:venue?.id,start:row.startsAt,end:row.endsAt};
    case "activity": return {...base,name:row.resource+(row.recordKey?" #"+row.recordKey:""),
      source:row.workflowActor||row.databaseActor,status:row.operation,description:row.changedFields.join(", ")};
    case "sports": return {...base,name:row.title,city:venue?.city,category:"Sport event",venue:venue?.name,
      venueRef:venue?.id,latitude:venue?.latitude,longitude:venue?.longitude,area:venue?.address,
      date:row.eventDate.slice(0,10),time:row.eventDate.slice(11,16),price:row.ticketPrice,
      slotRef:slot?.id,slot:slot?.name,capacity:row.ticketCapacity,availability:row.availableTickets,schedule:row.slotId?(row.ticketCapacity?"Scheduled":"Capacity needed"):"Slot needed"};
    case "jobs": return {...base,name:row.title,category:row.industry,city:company?.city||"casablanca",businessRef:company?.id,
      employer:company?.name,employment:row.employmentType,area:company?.address,
      latitude:company?.latitude,longitude:company?.longitude,deadline:row.applicationDeadline};
    case "news": return {...base,name:row.title,business:company?.name,businessRef:company?.id,city:company?.city,
      description:row.body,date:row.publishedAt?.slice(0,10),category:row.category==="NEWS"?"News":"Announcement"};
    case "bookings": {
      const event=find("sports",row.eventId);
      return {...base,name:event?.name||"Reservation "+row.id,venue:event?.venue,date:row.bookingDate.slice(0,10),
        total:(row.quantity*Number(row.unitPrice)).toFixed(2),owner:"Account #"+row.userId,status:statusLabel(row.status)};
    }
    case "applications": {
      const job=find("jobs",row.jobId);
      return {...base,name:job?.name||"Application "+row.id,cv:"CV #"+row.cVId,date:row.applicationDate.slice(0,10),
        cvId:row.cVId,note:row.coverNote,owner:"Account #"+row.userId,status:statusLabel(row.status)};
    }
  }
}

const statusLabel=status=>status?status[0]+status.slice(1).toLowerCase():"";

export function catalogPayload(section,row){
  const location={cityId:dbId(row.cityRef)};
  switch(section){
    case "cities": return row;
    case "tourism": return {...row,...location,openingHours:row.hours||"",rating:row.rating??0};
    case "education": return {...row,...location,institutionType:row.category,contactEmail:row.email};
    case "venues": return {...row,...location,venueType:row.category};
    case "business": return {...row,city:row.cityName,title:row.category};
    case "slots": return {venueId:dbId(row.venueRef),startsAt:row.start,endsAt:row.end};
    case "sports": return {...row,title:row.name,venueId:dbId(row.venueRef),ticketPrice:row.price,
      slotId:dbId(row.slotRef),ticketCapacity:row.capacity};
    case "jobs": return {...row,title:row.name,industry:row.category,employmentType:row.employment,companyId:dbId(row.businessRef),
      applicationDeadline:row.deadline||null};
    case "news": return {companyId:dbId(row.businessRef),title:row.name,body:row.description,
      category:row.category.toUpperCase(),published:row.published};
  }
}
