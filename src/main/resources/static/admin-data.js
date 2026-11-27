export const adminGroups = [
  ["Overview",["overview"]],
  ["People",["applications","bookings"]],
  ["City directory",["cities","tourism","education","jobs","business","news"]],
  ["Sport venues",["venues","slots","sports"]],
  ["History",["activity","analytics"]]
];

export const adminColumns = {
  cities:["name","country","latitude","longitude"],
  tourism:["name","category","city"],education:["name","category","city"],
  jobs:["name","employer","deadline"],business:["name","industry","city"],
  news:["name","business","date","published"],
  venues:["name","category","city","capacity"],slots:["name","venue","start","end"],
  sports:["name","venue","date","schedule","availability"],
  bookings:["name","owner","quantity","total","status"],applications:["name","owner","cv","date","status"],
  activity:["name","source","date","status","description"]
};
