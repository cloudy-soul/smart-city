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
  overview:"Dashboard", users:"Users and roles", cities:"Cities", tourism:"Tourism records",
  education:"Education records", sports:"Sport events", venues:"Venues", slots:"Venue time slots",
  jobs:"Job listings", business:"Business listings", news:"Business news", forwarding:"Enquiry forwarding",
  bookings:"Bookings", applications:"Applications", activity:"Activity log"
};

const sample = (id,module,name,category,details={}) => ({
  id,module,name,category,city:"casablanca",published:true,example:true,...details
});
const dates = offset => new Date(Date.now()+offset*86400000).toISOString().slice(0,10);
const venue="Mohammed V Stadium";

export const seed = [
  sample("mosque","tourism","Hassan II Mosque","Landmark",{area:"Waterfront",latitude:33.6086,longitude:-7.6327,description:"Visit the mosque on Casablanca’s Atlantic waterfront."}),
  sample("habous","tourism","Habous Quarter","Shopping",{area:"Habous",description:"Bookshops, craft stalls and covered arcades."}),
  sample("hotel","tourism","Waterfront hotel","Accommodation",{area:"Waterfront",description:"Sample hotel listing for a stay near the water."}),
  sample("cafe","tourism","Medina café","Restaurant",{area:"Old Medina",description:"Sample café listing."}),
  sample("atm","tourism","Neighbourhood cash machine","ATM",{area:"Centre",description:"Sample ATM directory record."}),
  sample("theatre","tourism","City theatre","Culture",{area:"Centre",description:"Sample theatre listing with performances and visiting information."}),
  sample("tram","tourism","Casablanca tramway","Transport",{description:"Find network and service information.",website:"https://www.casatramway.ma/"}),
  sample("clinic","tourism","City clinic","Healthcare",{area:"Centre",description:"Sample healthcare listing. Verify current services through an official provider."}),
  sample("emergency","tourism","Emergency services information","Emergency services",{description:"Directory entry for official emergency information. Confirm current contact information before travel."}),
  sample("rental","tourism","Car rental desk","Rental",{area:"Airport",description:"Sample rental listing for onward travel."}),
  sample("flight","tourism","Mohammed V International Airport","Air travel",{area:"Airport",description:"Find flight and airport information.",website:"https://www.onda.ma/"}),
  sample("university","education","Hassan II University","University",{description:"University information and contacts.",website:"https://www.univh2c.ma/"}),
  sample("library","education","Neighbourhood library","Library",{area:"Centre",description:"Sample study and reference facility."}),
  sample("college","education","City college","College",{area:"Centre",description:"Sample college listing."}),
  sample("coaching","education","Study support centre","Coaching centre",{description:"Sample tutoring and exam preparation listing."}),
  sample("training","education","Professional training centre","Training centre",{description:"Sample vocational training listing."}),
  sample("event","sports","Casablanca football fixture","Football",{venue,area:"Maârif",latitude:33.5821,longitude:-7.6465,date:dates(14),time:"18:00",capacity:200,availability:48,price:80,description:"A sample fixture for trying the reservation screens."}),
  sample("run","sports","Community running meet","Running",{venue:"City athletics track",date:dates(21),time:"09:00",availability:30,price:0,description:"Sample track event."}),
  sample("job","jobs","Junior web developer","Technology",{employer:"Sample digital studio",area:"Centre",employment:"Internship",deadline:dates(28),requirements:"HTML, CSS and JavaScript fundamentals. Include a current CV.",description:"Work on small web projects with the development team."}),
  sample("host","jobs","Front desk assistant","Hospitality",{employer:"Sample guesthouse",area:"Waterfront",employment:"Full-time",deadline:dates(19),requirements:"French and Arabic communication skills.",description:"Support guests with everyday enquiries."}),
  sample("finance","business","Casablanca Finance City","Business centre",{industry:"Financial services",description:"Information about the financial district.",website:"https://casablancafinancecity.com/"}),
  sample("studio","business","Digital services studio","Company",{area:"Centre",industry:"Technology",description:"Sample company profile with an enquiry form."}),
  sample("industry","business","Hospitality sector overview","Industry",{industry:"Hospitality",description:"Sample city industry listing."}),
  sample("market","business","Local suppliers opportunity","Opportunity",{description:"Sample business opportunity for independent suppliers."}),
  sample("opening","news","Local trade and networking","News",{business:"Casablanca Finance City",date:dates(7),description:"Example announcement of a local business networking session."}),
  sample("briefing","news","Small-business information session","Announcement",{business:"Digital services studio",date:dates(10),description:"Example event notice for local businesses."}),
  sample("stadium","venues",venue,"Stadium",{area:"Maârif",capacity:45000,latitude:33.5821,longitude:-7.6465,description:"Football venue in Casablanca."}),
  sample("slot","slots","Evening football slot","Available",{venue,start:dates(14)+"T18:00",end:dates(14)+"T20:00",status:"Available",price:80}),
  sample("enquiry","forwarding","Studio enquiries","Email",{business:"Digital services studio",destination:"studio@example.test",enabled:true}),
  sample("casa","cities","Casablanca","City",{country:"Morocco",latitude:33.59,longitude:-7.62}),
  sample("example-user","users","Sample registered user","User",{email:"traveller@example.test",role:"Registered user",enabled:true}),
  sample("example-admin","users","Sample administrator","User",{email:"admin@example.test",role:"Administrator",enabled:true})
];
