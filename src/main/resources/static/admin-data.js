// Local preview relationships use stable record IDs. Legacy sample rows may
// still carry only display names until they are edited.
export const adminGroups = [
  ["Overview",["overview"]],
  ["People",["users","applications","bookings"]],
  ["City directory",["cities","tourism","education","jobs","business","news","forwarding"]],
  ["Sport venues",["venues","slots","sports"]],
  ["History",["activity"]]
];

export const adminColumns = {
  users:["name","email","role","enabled"], cities:["name","country","latitude","longitude"],
  tourism:["name","category","city","published"],education:["name","category","city","published"],
  jobs:["name","employer","deadline","published"],business:["name","industry","city","published"],
  news:["name","business","date","published"],forwarding:["name","business","category","enabled"],
  venues:["name","category","city","capacity"],slots:["name","venue","start","end","status"],
  sports:["name","venue","date","availability","published"],
  bookings:["name","owner","quantity","total","status"],applications:["name","owner","cv","date","status"],
  activity:["name","source","date","status"]
};

export const adminRelations={
  sports:[{key:"slotId",label:"Venue time slot",module:"slots"}],
  slots:[{key:"venueId",label:"Venue",module:"venues"}],
  news:[{key:"businessId",label:"Business",module:"business"}],
  forwarding:[{key:"businessId",label:"Business",module:"business"}],
  jobs:[{key:"businessId",label:"Business (optional)",module:"business"}]
};

const byModule=(records,module,id)=>records.find(record=>record.module===module&&record.id===id);
const sameCity=(a,b)=>a.city===b.city;
const matches=(row,idKey,oldKey,target)=>row[idKey]?
  row[idKey]===target.id : Boolean(oldKey&&sameCity(row,target)&&row[oldKey]===target.name);

export function dependentRecord(records,target){
  const relationships={
    venues:[["slots","venueId","venue"],["sports","venueId","venue"]],
    slots:[["sports","slotId",null]],
    business:[["jobs","businessId",null],["news","businessId","business"],["forwarding","businessId","business"]],
    sports:[["bookings","eventId","name"]],
    jobs:[["applications","jobId","name"]],
    users:[["bookings","userId",null],["applications","userId",null]]
  };
  if(target.module==="cities") {
    const city=target.city;
    return records.find(row=>row!==target&&row.city===city);
  }
  if(target.module==="users"){
    const emailMatch=records.find(row=>["bookings","applications"].includes(row.module)&&row.owner===target.email);
    if(emailMatch)return emailMatch;
  }
  return (relationships[target.module]||[]).map(([module,idKey,oldKey])=>
    records.find(row=>row.module===module&&matches(row,idKey,oldKey,target))
  ).find(Boolean);
}

export function validateAdminRecord(records,section,record,updates){
  const merged={...record,...updates};
  if((merged.latitude!=null&&Math.abs(merged.latitude)>90)||
     (merged.longitude!=null&&Math.abs(merged.longitude)>180)||
     ((merged.latitude==null)!==(merged.longitude==null)))return "Enter both coordinates within their valid ranges.";
  if(section==="users"){
    const email=merged.email?.toLowerCase();
    if(!email)return "Enter an email address.";
    if(records.some(row=>row!==record&&row.module==="users"&&row.email?.toLowerCase()===email)){
      return "Another user already has this email address.";
    }
    if(merged.role==="Administrator"&&![1,2,3].includes(merged.authorizationLevel)){
      return "Choose an administrator authorization level from 1 to 3.";
    }
  }
  if(section==="venues"&&merged.capacity!=null&&(!Number.isInteger(merged.capacity)||merged.capacity<=0)){
    return "Venue capacity must be a positive whole number.";
  }
  if(section==="tourism"&&merged.rating!=null&&(merged.rating<0||merged.rating>5)){
    return "Enter a rating from 0 to 5.";
  }
  if(["sports","slots","jobs"].includes(section)&&
    [merged.price,merged.salary].some(value=>value!=null&&value<0))return "Price and salary must be zero or higher.";

  const relation=adminRelations[section]?.[0];
  const related=relation&&byModule(records,relation.module,merged[relation.key]);
  if(relation&&merged[relation.key]&&!related)return "Choose a related record from the list.";
  if(relation&&!related&&!record&&section!=="jobs")return "Choose a related record before saving.";

  if(section==="slots"){
    if(!merged.start||!merged.end||merged.end<=merged.start)return "The slot must end after it starts.";
    if(related&&records.some(row=>row!==record&&row.module==="slots"&&matches(row,"venueId","venue",related)&&
      row.start<merged.end&&row.end>merged.start))return "This venue already has an overlapping time slot.";
  }
  if(section==="sports"){
    const {capacity,availability}=merged;
    if([capacity,availability].some(value=>value!=null&&(!Number.isInteger(value)||value<0))||
      (capacity!=null&&availability!=null&&availability>capacity))return "Available tickets must be a whole number between zero and total tickets.";
    const confirmed=record?records.filter(row=>row.module==="bookings"&&row.status==="Confirmed"&&matches(row,"eventId","name",record))
      .reduce((sum,row)=>sum+row.quantity,0):0;
    if(capacity!=null&&(confirmed>capacity||(availability!=null&&confirmed+availability>capacity))){
      return "Total tickets must cover confirmed reservations and available tickets.";
    }
    if(related&&records.some(row=>row!==record&&row.module==="sports"&&row.slotId===related.id)){
      return "This time slot is already assigned to another event.";
    }
  }
  return "";
}
export function fillRelatedFields(records,section,updates){
  const relation=adminRelations[section]?.[0];
  const parent=relation&&byModule(records,relation.module,updates[relation.key]);
  if(!parent)return updates;
  if(section==="slots")return {...updates,venue:parent.name,city:parent.city};
  if(section==="sports"){
    const venue=byModule(records,"venues",parent.venueId);
    return {...updates,venueId:parent.venueId,venue:venue?.name||parent.venue,slot:parent.name,
      city:parent.city,date:parent.start.slice(0,10),time:parent.start.slice(11,16),
      latitude:venue?.latitude??updates.latitude,longitude:venue?.longitude??updates.longitude,
      address:venue?.address||updates.address};
  }
  if(section==="jobs")return {...updates,employer:updates.employer||parent.name,city:parent.city};
  return {...updates,business:parent.name,city:parent.city};
}
export function updateBookingStatus(records,booking,status){
  if(booking.status===status)return "";
  const event=byModule(records,"sports",booking.eventId) ||
    (!booking.eventId&&records.find(row=>row.module==="sports"&&matches(booking,"eventId","name",row)));
  if(event&&Number.isFinite(event.availability)){
    if(status==="Confirmed"&&event.availability<booking.quantity)return "There are too few tickets left to confirm this booking.";
    if(booking.status==="Confirmed"){
      event.availability=Math.min(event.capacity??Infinity,event.availability+booking.quantity);
    }
    if(status==="Confirmed")event.availability-=booking.quantity;
  }
  booking.status=status;
  return "";
}

export function refreshRelatedRecords(records){
  for(const section of ["slots","sports","jobs","news","forwarding"]){
    for(const row of records.filter(record=>record.module===section)){
      Object.assign(row,fillRelatedFields(records,section,row));
    }
  }
  for(const row of records){
    if(row.module==="bookings"){
      const event=byModule(records,"sports",row.eventId);
      if(event)Object.assign(row,{name:event.name,venue:event.venue,date:event.date});
    }else if(row.module==="applications"){
      const job=byModule(records,"jobs",row.jobId);
      if(job)row.name=job.name;
    }
  }
}
export function linkLegacyRecords(records){
  const record=(module,city,name)=>records.find(row=>row.module===module&&row.city===city&&row.name===name);
  for(const row of records){
    if(row.module==="slots"&&!row.venueId)row.venueId=record("venues",row.city,row.venue)?.id;
    if(["news","forwarding"].includes(row.module)&&!row.businessId)row.businessId=record("business",row.city,row.business)?.id;
    if(row.module==="sports"){
      row.venueId ||= record("venues",row.city,row.venue)?.id;
      row.slotId ||= records.find(slot=>slot.module==="slots"&&sameCity(row,slot)&&slot.venue===row.venue&&
        slot.start===row.date+"T"+row.time)?.id;
    }
    if(row.module==="bookings"&&!row.eventId)row.eventId=record("sports",row.city,row.name)?.id;
    if(row.module==="applications"&&!row.jobId)row.jobId=record("jobs",row.city,row.name)?.id;
  }
}
