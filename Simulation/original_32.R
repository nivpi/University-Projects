.libPaths("D:/soft/r/4.2")#this row only for labs
library(rlang)
library(MASS)
library(fitdistrplus)
library(magrittr)
library(dplyr)
library(lazyeval)
library(parallel)
library(e1071)
library(plotly)
library(ggplot2)
library(triangle)
library(sqldf)
library(readxl)
 library(knitr)
 library(rmarkdown)
library(simmer)
library(simmer.plot)

#ID members of group: 205918477_318234481_313520389

### calculate and choose distribution for entire time of couples and families based on excel

#read data of family and couple
data <- read.csv(file.choose(), header = T)

diff_couples<-data$couples_diff/60 #converting to minutes from seconds
diff_families<-data$families_diff/60

#create fitdist for some distribution
couple_Fit_exp <- fitdist(diff_couples,"exp")
# couple_Fit_norm <- fitdist(diff_couples,"norm") 
# couple_Fit_unif <- fitdist(diff_couples,"unif")
# summary(couple_Fit_exp)
# summary(couple_Fit_norm)
# summary(couple_Fit_unif)
# plotdist(diff_couples, "exp", para = list(rate = couple_Fit_exp$estimate[1]))
# plotdist(diff_couples, "norm", para = list(mean = couple_Fit_norm$estimate[1], sd = couple_Fit_norm$estimate[2]))
# plotdist(diff_couples, "unif", para = list(min = couple_Fit_unif$estimate[1], max = couple_Fit_unif$estimate[2]))
print(paste("Best distribution for couples is EXP with lambda:",round(couple_Fit_exp$estimate[1], 5)))
lambda_couple <- as.numeric(couple_Fit_exp$estimate[1])

family_Fit_exp <- fitdist(diff_families,"exp")
# family_Fit_norm <- fitdist(diff_families,"norm")
# family_Fit_unif <- fitdist(diff_families,"unif")
# summary(family_Fit_exp)
# summary(family_Fit_norm)
# summary(family_Fit_unif)
# plotdist(diff_families, "exp", para = list(rate = family_Fit_exp$estimate[1]))
# plotdist(diff_families, "norm", para = list(mean = family_Fit_norm$estimate[1], sd = family_Fit_norm$estimate[2]))
# plotdist(diff_families, "unif", para = list(min = family_Fit_unif$estimate[1], max = family_Fit_unif$estimate[2]))
print(paste("Best distribution for families is EXP with lambda:",round(family_Fit_exp$estimate[1], 5)))
lambda_family <- as.numeric(family_Fit_exp$estimate[1])

##----------------------------------------- 1.  all functions ------------------------------------------------

addService<- function (path,sname,timeDist){ # catch a source
  updatedPath <- seize(path, sname)%>%
    timeout(timeDist)%>%
    release(sname)
  return(updatedPath)
}

trimmedNorm<-function (mu,sd){ # trimmed norm
  while(TRUE){
    sample<-rnorm(1,mu,sd)
    if (sample>0)
      return (sample)
  }
}

# choose favorite food for guest
favouriteFoods <- function() {
  y = 0
  if (get_attribute(weddingSimulation,"type") == 1){ #single will save a place for main dishes
    y <- sample(partial_foods)
  }
  else {y <- sample(foods)} #couple or family
  return (y)
}

# return type of guest by number of guests
get_type <- function(){
  name<- get_name(weddingSimulation)
  x <- 0
  if (substr(name,0,1) == "f"){ # family
    x <- rdiscrete(1,c(0.33,0.4,0.27), c(3,4,5))
  }
  else if (substr(name,0,1) == "c") {x <- 2} #couple
  else {x <- 1} #single
  return (c(x))
}

chanceForBarByType <- function(){ #return the percentage for going to the bar
  y = 0.0
  if (get_attribute(weddingSimulation,"type") == 1) {y <- 0.8} #single
  else if (get_attribute(weddingSimulation,"type") == 2) {y <- 0.6} #couple
  else {y <- 0.4} #family
  return (y)
}

getType <- function(){ # an auxiliary function that helps with routing when entering the hall
  y = 0
  if (get_attribute(weddingSimulation,"type") > 2) {y <- 0} #family
  else {y <- 1} #single or couple
  return (y)
}

get_food <- function(){ #return for each food attribute the number of road plan to go
  counter <- get_attribute(weddingSimulation,"counter")
  food <- as.integer(get_attribute(weddingSimulation,paste("food",counter,sep=""))) 
  return (food)
}

groupEatingTime<-function(){ #The function samples the eating times of all 
  #members of the family/couple/individual and returns
  #the highest eating time among all members of the group
  familyMembers<- get_attribute(weddingSimulation,"type") # number of guest in the group
  t<- rdiscrete(familyMembers,c(0.7,0.3),c(1,2)) # with a probability of 70 percent -
  #a meat dish and with a probability of
  #30 percent - a vegetarian dish
  x<-c(0,0,0,0,0)
  n=1
  for(i in t){
    if(i==1) {x[n]<-trimmedNorm(12,3)} #meat
    else {x[n]<-trimmedNorm(7,4)} #vegetarian
    n=n+1
  }
  maxTime <-max(x) #the highest from group's member
  return(maxTime)
}

satietyGenerator <- function() { # satiety function
  selector <- runif(1)
  u <- runif(1)
  
  if (selector < 15/30) #f1
    ans = (7*u+1)^(1/3)
  else if (selector > 15/30 & selector < 21/30)   #f2
    ans = sqrt(3*u+1)
  else if (selector > 21/30 & selector < 23/30)   #f3
    ans = u+1
  else if (selector > 23/30 & selector < 28/30)   #g
    ans = 20/6 - sqrt(16/9-5/3*u)
  else
    ans = 5 - 2*sqrt(1-u)
  
  return (ans)
}

groupSatiety <- function (n = 1) {
  return (sum(replicate(n,satietyGenerator())))
}

returnSatiety<- function(){
  x<-get_attribute(weddingSimulation,"Satiety_level")
  y <- 0
  if (x < 4.5) {y <- 1}
  return (c(y,y,FALSE))
}

# check_singles <- function(){
#   singlesQuntity<-floor(runif(1,100,151))
#   return (singlesQuntity)
# }

##----------------------------------------- 2.  all simulation parameters ------------------------------------------------

simulationTime <- 6*60

# busTime <- runif(1,60,75)
# singlesQuntity<-floor(runif(1,100,151))
# weddingTime <- runif(1,140,160)
# weddingLength <- runif(1,20,35)
# huppaSchedule <- schedule(timetable = c(0,weddingTime+weddingLength), values = c(0,Inf)) #תזמון זמני החופה כיוון שבחופה כולם ממתינים

foods <- c(1,2,3,4,5) # c("tortilla","veg","sushi","bun","focaccia")
partial_foods <- c(1,2,3,0,0)


##----------------------------------------- 3.  Init Simulation and add all resources  ------------------------------------------------

weddingSimulation<- simmer("sim name")%>%
  add_resource("reception1",capacity=2,queue_size=Inf)%>%
  add_resource("reception2",capacity=2,queue_size=Inf)%>%
  add_resource("focaccias",capacity=3,queue_size=Inf)%>%
  add_resource("buns",capacity=3,queue_size=Inf)%>%
  add_resource("sushi1",capacity=1,queue_size=Inf)%>%
  add_resource("sushi2",capacity=2,queue_size=Inf)%>%
  add_resource("vegs",capacity=4,queue_size=Inf)%>%
  add_resource("tortillas",capacity=4,queue_size=Inf)%>%
  add_resource("externalBar",capacity=3,queue_size=Inf)%>%
  add_resource("internalBar1",capacity=5,queue_size=Inf)%>%
  add_resource("internalBar2",capacity=7,queue_size=Inf)%>%
  add_resource("desserts",capacity=8,queue_size=7, preemptive = FALSE)%>%
  add_resource("parking",capacity=Inf,queue_size =Inf)%>%
  add_resource("huppa",capacity=0,queue_size =Inf)

##----------------------------------------- 4.  All trajectories, start from main trajectory and add sub-trajectories ABOVE IT it . ------------------------------------------------
dessertPath<- trajectory("dessertPath")%>%
  # log_("I catch the dessert")%>%
  timeout(function() runif(1,2.5,4))%>% #people eat in dessert area
  release("desserts",1)%>%
  set_attribute("disappointed", 0) # not disappointed

dancePath <- trajectory("dancePath")%>%
  # log_("im dancing")%>%
  timeout(function() rtriangle(1,5, 10, 7)) # dancing

internalBarPath<-trajectory("internalBarPath")%>%
  simmer::select(resources=c("internalBar1","internalBar2"),  policy=c("shortest-queue") )%>%
  #log_("im in internalBar")%>%
  seize_selected(amount = 1) %>%
  timeout(rexp(1,2.4)) %>%
  release_selected(amount = 1)

externalBar <- trajectory("externalBar")%>%
  #log_("Im drinking at external bar")%>%
  addService("externalBar", function () rexp(1,2))

bunsPath<-trajectory("bunsPath")%>%
  #log_("im in bunPath")%>%
  addService("buns", function() trimmedNorm(1.5, 0.7))%>%
  timeout(rexp(1,0.8))%>% #time for eating the food
  branch (option = function() rdiscrete(1,c(1 - chanceForBarByType(),chanceForBarByType()),c(0,1)), continue = c(TRUE) ,externalBar)%>%
  timeout(0)

focacciaPath<-trajectory("focacciaPath")%>%
  #log_("im in focacciaPath")%>%
  addService("focaccias", function() trimmedNorm(1.5, 0.7))%>%
  timeout(rexp(1,0.8))%>%
  branch (option = function() rdiscrete(1,c(1 - chanceForBarByType(),chanceForBarByType()),c(0,1)), continue = c(TRUE) ,externalBar)%>%
  timeout(0)

vegsPath<-trajectory("vegsPath")%>%
  #log_("im in vegPath")%>%
  addService("vegs", function() trimmedNorm(1.5, 0.7))%>%
  timeout(rexp(1,0.8))%>%
  branch (option = function() rdiscrete(1,c(1 - chanceForBarByType(),chanceForBarByType()),c(0,1)), continue = c(TRUE) ,externalBar)%>%
  timeout(0)

tortillasPath<-trajectory("tortillasPath")%>%
  #log_("im in tortillasPath")%>%
  addService("tortillas", function() trimmedNorm(1.5, 0.7))%>%
  timeout(rexp(1,0.8))%>%
  branch (option = function() rdiscrete(1,c(1 - chanceForBarByType(),chanceForBarByType()),c(0,1)), continue = c(TRUE) ,externalBar)%>%
  timeout(0)

sushiPath<-trajectory("sushiPath")%>%
  simmer::select(resources=c("sushi1","sushi2"),  policy=c("shortest-queue-available") )%>%
  #log_("im in sushiPath")%>%
  seize_selected(amount = 1) %>%
  timeout(trimmedNorm(1.5, 0.7)) %>%
  release_selected(amount = 1)%>%
  timeout(rexp(1,0.8))%>%
  branch (option = function() rdiscrete(1,c(1 - chanceForBarByType(),chanceForBarByType())
                                        ,c(0,1)), continue = c(TRUE) ,externalBar)%>%
  timeout(0)

reception<- trajectory("reception")%>%
  simmer::select(resources=c("reception1","reception2"),  policy=c("shortest-queue") )%>%
  #log_("im in reception")%>%
  seize_selected(amount = 1) %>%
  timeout(trimmedNorm(0.5, 0.2)) %>%
  release_selected(amount = 1)%>%
  leave(0.07)%>%
  set_attribute("table", function () floor(runif(1,1,96)))

mainPath<- trajectory("mainPath")%>%
  set_attribute("type", function () get_type())%>%
  join(reception)%>%
  #log_("Im finish the reception")%>%
  set_attribute("disappointed", 1)%>% # disappointed
  clone(function() get_attribute(weddingSimulation, "type"))%>%
  set_attribute(keys=c("food1","food2","food3","food4","food5"), values = function() favouriteFoods())%>%
  set_attribute("counter", 1)%>%
  branch(option = function() get_food(), continue = c(TRUE,TRUE,TRUE,TRUE,TRUE),
         tortillasPath,vegsPath,sushiPath,bunsPath,focacciaPath)%>% #split to food stands
  set_attribute("counter", 1, mod = "+")%>%
  rollback(times = 4, amount = 2)%>%
  # log_("finish dishes")%>%
  synchronize(wait = TRUE, mon_all = TRUE)%>%
  #log_("finish all dishes")%>%
  seize("huppa",1)%>%    # everyone waiting until the end of the huppa
  release("huppa",1)%>%  # huppa end
  # log_("huppa is over")%>%
  branch (option = function() getType(), continue = c(TRUE),
          internalBarPath)%>% # sending couples and singles to the internal bar
  timeout(rexp(1,1/3.5))%>%  # time to search for table
  # log_("arrived to table")%>%
  timeout(function() groupEatingTime())%>%
  # log_("finish to eat")%>%
  set_attribute("Satiety_level", function() groupSatiety(get_attribute(weddingSimulation,"type")))%>%
  set_prioritization(values = function() returnSatiety())%>%
  # log_("arrived to dessert queue")%>%
  seize("desserts",1, continue = c(FALSE, TRUE), post.seize = dessertPath,
        reject = dancePath)%>% #if catch dessert go to eat 
  rollback(times = 2, amount = 1)%>% #############################change amount to use halufa fruits
  #set_attribute("disappointed", 1)%>% # disappointed
  leave(1)

parkingPath <- trajectory("parkingPath") %>%
  #log_("im in parking")%>%
  addService("parking", function() rtriangle(1,3, 5, 4))%>%
  join(mainPath)

trigger<- trajectory() %>% 
  timeout_from_global("busTime")%>%
  activate("single")


resetPath <- trajectory("resetPath")%>%
  set_global(keys = "busTime", value = function() runif(1,60,75))%>%
  set_global(keys = "singlesQuntity", value = function() floor(runif(1,100,151)))%>%
  set_global(keys = "weddingTime", value = function() runif(1,140,160))%>%
  set_global(keys = "weddingLength", value = function() runif(1,20,35))%>%
  set_global(keys = "huppaEnd", value = function() get_global(weddingSimulation,key="weddingTime") + get_global(weddingSimulation, key="weddingLength"))%>%
  set_global(keys = "barClose", value = function() get_global(weddingSimulation,key="weddingTime") - 10)%>%
  set_queue_size("reception1",Inf)%>%set_capacity("reception1", 2)%>%
  set_queue_size("reception2",Inf)%>%set_capacity("reception2", 2)%>%
  set_queue_size("focaccias",Inf)%>%set_capacity("focaccias", 3)%>%
  set_queue_size("buns",Inf)%>%set_capacity("buns", 3)%>%
  set_queue_size("sushi1",Inf)%>%set_capacity("sushi1", 1)%>%
  set_queue_size("sushi2",Inf)%>%set_capacity("sushi2", 2)%>%
  set_queue_size("vegs",Inf)%>%set_capacity("vegs", 4)%>%
  set_queue_size("tortillas",Inf)%>%set_capacity("tortillas", 4)%>%
  set_queue_size("externalBar",Inf)%>%set_capacity("externalBar", 3)%>%
  set_queue_size("internalBar1",Inf)%>%set_capacity("internalBar1", 5)%>%
  set_queue_size("internalBar2",Inf)%>%set_capacity("internalBar2", 7)%>%
  set_queue_size("desserts",7)%>%set_capacity("desserts", 8)%>%
  set_queue_size("parking",Inf)%>%set_capacity("parking", Inf)%>%
  set_queue_size("huppa",Inf)%>%set_capacity("huppa", 0)%>%
  timeout_from_global("barClose")%>%
  set_queue_size("externalBar",0)%>%
  timeout(10)%>%
  set_queue_size("focaccias",0)%>%
  set_queue_size("buns",0)%>%
  set_queue_size("sushi1",0)%>%
  set_queue_size("sushi2",0)%>%
  set_queue_size("vegs",0)%>%
  set_queue_size("tortillas",0)%>%
  timeout_from_global("weddingLength")%>%
  set_capacity("huppa", Inf)
  
  
  

##----------------------------------------- 5.  All Generators, ALWAYS LAST. ------------------------------------------------
weddingSimulation%>%
  # add_generator("couple", parkingPath, at(5), mon=2) %>%
  add_generator("reset", resetPath, distribution = at(0))%>%
  add_generator("couple", parkingPath, distribution = to(4*60, function () rexp(1,lambda_couple)), mon=2)%>%
  add_generator("family", parkingPath, distribution = to(4*60, function () rexp(1,lambda_family)), mon=2)%>%
  add_generator("single", mainPath, when_activated(function() get_global(weddingSimulation, "singlesQuntity")), mon=2) %>%
  add_generator("trigger", trigger, at(0))


##----------------------------------------- 6.  reset, run, plots, outputs ------------------------------------------------

mm1envs <- mclapply(1:20, function(i){
  set.seed(455+i)
  reset(weddingSimulation)%>%run(until=simulationTime)%>%
    wrap()
})

# Arrivals Table
fullData <- get_mon_arrivals(mm1envs,ongoing = F, per_resource = T)%>%
  mutate(waiting_time = end_time - start_time - activity_time)
#Resource Table
resourceData <- get_mon_resources(mm1envs)
# Attribute Table
attributeData <- get_mon_attributes(mm1envs)

# Measure 1:  Average waiting time

Waiting_AVG <-sqldf("SELECT replication, AVG(waiting_time)
                  FROM fullData
                  WHERE resource NOT IN ('huppa', 'externalBar', 'internalBar1',
                  'internalBar2', 'desserts', 'parking', 'reception1', 'reception2')
                  GROUP BY replication
                  ORDER BY 1 ASC")

Measure1 <- Waiting_AVG$`AVG(waiting_time)`


# Measure 2:  % of disappointed guests

finishGuests <- sqldf("SELECT replication, COUNT(*) as finished
                      FROM attributeData
                      WHERE key = 'disappointed'
                      GROUP BY replication
		      ORDER BY 1 ASC")
disaGuests <- sqldf("SELECT replication, COUNT(*) as disa
                    FROM attributeData
                    WHERE key = 'disappointed' and value = 1
                    GROUP BY replication
		    ORDER BY 1 ASC")

Measure2 <- disaGuests$disa/finishGuests$finished


# Measure 3:  % of guests arrived to huppa in time

invitedGuests <- sqldf("SELECT replication, COUNT(*) as invited
                       FROM attributeData
                       WHERE key = 'table'
                       GROUP BY replication
                       ORDER BY 1 ASC")
arrivedHuppaOnTime <- sqldf("SELECT replication, MAX(queue) as arrived
                            FROM resourceData
                            WHERE resource = 'huppa'
                            ORDER BY 1 ASC")

Measure3 <- arrivedHuppaOnTime$arrived/invitedGuests$invited


# Mean & SD for all the Measures

mean_Measure1 <- mean(Measure1)
sd_Measure1 <- sd(Measure1)

mean_Measure2 <- mean(Measure2)
sd_Measure2 <- sd(Measure2)

mean_Measure3 <- mean(Measure3)
sd_Measure3 <- sd(Measure3)



# original number of running was 20
n0<-20
gamma_ <- 0.1
alfa_total <- 0.09
alfa_i <- alfa_total/3
t <- qt(1 - (alfa_i)/2,n0 -1)
gamma_tag <- gamma_/(1 + gamma_)

# calculate the relative accuracy
calc_relative_accuracy <- function(mean,sd){
  (t*sd/sqrt(n0))/mean
}

# calculate the rough number of needed iterations
number_of_replications <- function(relative_acuuracy){
  n0*(relative_acuuracy/gamma_tag)^2
}


# relative accuracy for each measure
relative_accuracy_measure1 <- calc_relative_accuracy(mean_Measure1, sd_Measure1)
relative_accuracy_measure2 <- calc_relative_accuracy(mean_Measure2, sd_Measure2)
relative_accuracy_measure3 <- calc_relative_accuracy(mean_Measure3, sd_Measure3)

# rough number of needed iterations for each measure
n_needed_measure1 <- number_of_replications(relative_accuracy_measure1)
n_needed_measure2 <- number_of_replications(relative_accuracy_measure2)
n_needed_measure3 <- number_of_replications(relative_accuracy_measure3)


############# Only for 1 Itearation

# set.seed(456)
# reset(weddingSimulation)%>%run(until=simulationTime)
# 
# arrivalData <- get_mon_arrivals(weddingSimulation,ongoing = F, per_resource = T)%>%
#      mutate(waiting_time = end_time - start_time - activity_time)
# resourceData <- get_mon_resources(weddingSimulation)
# attributeData <- get_mon_attributes(weddingSimulation)

# # Measure 1:  Average waiting time
# 
# Measure_1 <-sqldf("SELECT AVG(waiting_time)
#                   FROM arrivalData
#                   WHERE resource NOT IN ('huppa', 'externalBar', 'internalBar1',
#                   'internalBar2', 'desserts', 'parking', 'reception1', 'reception2')")
# 
# Measure1 <-Measure_1$`AVG(waiting_time)`
# 
# 
# 
# # Measure 2:  % of disappointed guests
# 
# finishGuests <- sqldf("SELECT COUNT(*) as finished
#                       FROM attributeData
#                       WHERE key = 'disappointed'")
# disaGuests <- sqldf("SELECT COUNT(*) as disa
#                     FROM attributeData
#                     WHERE key = 'disappointed' and value = 1")
# 
# Measure2 <- disaGuests$disa/finishGuests$finished
# 
# 
# 
# # Measure 3:  % of guests arrived to huppa in time
# 
# invitedGuests <- sqldf("SELECT COUNT(*) as invited
#                        FROM attributeData
#                        WHERE key = 'table'")
# arrivedHuppaOnTime <- sqldf("SELECT MAX(queue) as arrived
#                             FROM resourceData
#                             WHERE resource = 'huppa'")
# 
# Measure3 <- arrivedHuppaOnTime$arrived/invitedGuests$invited



