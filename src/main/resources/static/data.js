export const modules = {
  tourism: { label:"Tourism", singular:"Place", categories:["Landmark","Accommodation","Restaurant","ATM","Culture","Transport","Healthcare","Emergency services","Shopping","Rental","Air travel"] },
  education: { label:"Education", singular:"Institution", categories:["University","College","Library","Coaching centre","Training centre"] },
  sports: { label:"Sport events", singular:"Event", categories:["Football","Basketball","Running","Swimming","Other sport"] },
  jobs: { label:"Jobs", singular:"Job listing", categories:["Technology","Hospitality","Finance","Retail","Education","Transport","Other industry"] },
  business: { label:"Business", singular:"Business listing", categories:["Company","Business centre","Industry","Opportunity","News","Announcement"] }
};

const f = (key,label,type="text",options) => ({key,label,type,options});
const opts = (key,label,options) => f(key,label,"select",options);
const coordinates = [f("latitude","Latitude","number"),f("longitude","Longitude","number")];
const place = [f("name","Name"),f("area","Area"),f("address","Address"),...coordinates,f("description","Description","textarea")];
const published = f("published","Published","checkbox");
const contact = [f("contact","Contact email","email"),f("phone","Phone","tel"),f("website","Website","url")];

export const schemas = {
  tourism: [opts("category","Category",modules.tourism.categories),...place,f("hours","Opening hours"),f("rating","Rating","number"),...contact,published],
  education: [opts("category","Institution type",modules.education.categories),...place,...contact,published],
  sports: [opts("category","Sport",modules.sports.categories),...place,f("venue","Venue"),f("slot","Venue time slot"),f("date","Event date","date"),f("time","Start time","time"),f("capacity","Total tickets","number"),f("availability","Available tickets","number"),f("price","Ticket price (MAD)","number"),published],
  jobs: [opts("category","Industry",modules.jobs.categories),...place,f("employer","Employer"),opts("employment","Employment type",["Full-time","Part-time","Internship","Contract"]),f("salary","Salary (MAD)","number"),f("deadline","Application deadline","date"),f("requirements","Requirements","textarea"),published],
  business: [opts("category","Category",modules.business.categories),...place,f("industry","Industry"),...contact,published],
  venues: [...place,f("category","Venue type"),f("capacity","Capacity","number"),published],
  slots: [f("name","Label"),f("venue","Venue"),f("start","Start time","datetime-local"),f("end","End time","datetime-local"),f("price","Price (MAD)","number"),opts("status","Status",["Available","Reserved"])],
  news: [f("name","Headline"),f("business","Business"),opts("category","Type",["News","Announcement","Opportunity"]),f("description","Body","textarea"),f("date","Publication date","date"),published],
  forwarding: [f("name","Label"),f("business","Business"),opts("category","Forwarding type",["Email","Webhook"]),f("destination","Destination"),f("enabled","Active","checkbox")],
  cities: [...place,f("country","Country")],
  users: [f("name","Full name"),f("email","Email","email"),f("phone","Phone","tel"),f("country","Country"),opts("role","Role",["Registered user","Student","Tourist","Administrator"]),f("enabled","Enabled","checkbox")],
  bookings: [f("name","Event"),f("owner","Account"),f("venue","Venue"),f("date","Event date","date"),f("quantity","Tickets","number"),f("total","Total (MAD)","number"),opts("status","Status",["Pending","Confirmed","Cancelled"])],
  applications: [f("name","Job"),f("owner","Account"),f("cv","CV"),f("date","Submitted on","date"),f("note","Cover note","textarea"),opts("status","Status",["Received","Reviewed","Accepted","Rejected"])],
  activity: [f("name","Action"),f("source","Source"),f("date","Time"),f("status","Status"),f("description","Details","textarea")],
  cv: [f("name","CV title"),f("description","Description","textarea"),f("skills","Skills"),f("keywords","Keywords"),f("file","CV file","file")],
  profile: [f("name","Full name"),f("email","Email","email"),f("phone","Phone","tel"),f("country","Country"),opts("language","Preferred language",["English","Français","العربية"]),f("studentNumber","Student number"),f("institution","Institution"),f("travelPurpose","Travel purpose","textarea")]
};

export const adminSections = {
  overview:"Dashboard", analytics:"Analytics", cities:"Cities", tourism:"Tourism records",
  education:"Education records", sports:"Sport events", venues:"Venues", slots:"Venue time slots",
  jobs:"Job listings", business:"Business listings", news:"Business news",
  bookings:"Bookings", applications:"Applications", activity:"Activity log"
};
