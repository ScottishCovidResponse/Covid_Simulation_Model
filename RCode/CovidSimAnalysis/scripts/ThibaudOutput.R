library(reshape2)

baseline <- baseline %>% group_by(iter) %>% mutate(cumDHome = cumsum(DHome))
baseline <- baseline %>% group_by(iter) %>% mutate(cumDHosp = cumsum(DHospital))
baseline <- baseline %>% group_by(iter) %>% mutate(cumDCHome = cumsum(DCareHome))
baseline$cumDeaths <- baseline$cumDCHome + baseline$cumDHome + baseline$cumDHosp

baselineR <- baseline %>% filter(day == 160) %>% arrange(cumDeaths)

sIter <- baselineR$iter[c(1,50,100)]
baselineThibaud <- baseline %>% filter(iter %in% sIter, day <= 160) %>% mutate(Deaths = DHome + DHospital + DCareHome, Hospitalised = HospitalisedToday) %>% select(iter, day, Deaths, Hospitalised)

baselineThibaudC <- dcast(baselineThibaud, day ~ iter, value.var = "Deaths")
baselintThibaudT <- as.data.frame(t(baselineThibaudC))

write.csv(baselintThibaudT, "ThibaudExamples.csv", row.names = F, na = "")
