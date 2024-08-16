
original <- read.csv(file.choose(), header = T)
halufa1 <- read.csv(file.choose(), header = T)
halufa2 <- read.csv(file.choose(), header = T)


# Measure 1 Comparison

measure1_original_halufa1 <- t.test(original$Measure1, halufa1$Measure1, alternative = "two.sided",
                                    paired = TRUE, var.equal = TRUE, conf.level = 0.97)
print(measure1_original_halufa1)

measure1_original_halufa2 <- t.test(original$Measure1, halufa2$Measure1, alternative = "two.sided",
                                    paired = TRUE, var.equal = TRUE, conf.level = 0.97)
print(measure1_original_halufa2)

measure1_halufa1_halufa2 <- t.test(halufa1$Measure1, halufa2$Measure1, alternative = "two.sided",
                                   paired = TRUE, var.equal = TRUE, conf.level = 0.97)
print(measure1_halufa1_halufa2)


# Measure 2 Comparison

measure2_original_halufa1 <- t.test(original$Measure2, halufa1$Measure2, alternative = "two.sided",
                                    paired = TRUE, var.equal = TRUE, conf.level = 0.97)
print(measure2_original_halufa1)

measure2_original_halufa2 <- t.test(original$Measure2, halufa2$Measure2, alternative = "two.sided",
                                    paired = TRUE, var.equal = TRUE, conf.level = 0.97)
print(measure2_original_halufa2)

measure2_halufa1_halufa2 <- t.test(halufa1$Measure2, halufa2$Measure2, alternative = "two.sided",
                                   paired = TRUE, var.equal = TRUE, conf.level = 0.97)
print(measure2_halufa1_halufa2)


# Measure 3 Comparison

measure3_original_halufa1 <- t.test(original$Measure3, halufa1$Measure3, alternative = "two.sided",
                                    paired = TRUE, var.equal = TRUE, conf.level = 0.97)
print(measure3_original_halufa1)

measure3_original_halufa2 <- t.test(original$Measure3, halufa2$Measure3, alternative = "two.sided",
                                    paired = TRUE, var.equal = TRUE, conf.level = 0.97)
print(measure3_original_halufa2)

measure3_halufa1_halufa2 <- t.test(halufa1$Measure3, halufa2$Measure3, alternative = "two.sided",
                                   paired = TRUE, var.equal = TRUE, conf.level = 0.97)
print(measure3_halufa1_halufa2)




# Measure 1 compression original and Halufa 1

# ci1<-c(measure1_original_halufa1$conf.int[1],measure1_original_halufa1$conf.int[2])%>%print
# meanFlow1 <- measure1_original_halufa1$estimate%>%print
# delta1 <- measure1_original_halufa1$conf.int[2]-meanFlow1%>%print
# diuk1 <- delta1/meanFlow1
# print(paste0("The relative accuracy for average waiting time for Current situation is " ,diuk1))


# Measure 1 compression original and Halufa 2

# ci2<-c(measure1_original_halufa2$conf.int[1],measure1_original_halufa2$conf.int[2])%>%print
# meanFlow2 <- measure1_original_halufa2$estimate%>%print
# delta2 <- measure1_original_halufa2$conf.int[2]-meanFlow2%>%print
# diuk2 <- delta2/meanFlow2
# print(paste0("The relative accuracy for average waiting time for Current situation is " ,diuk2))


# Measure 1 compression Halufa 1 and Halufa 2

# ci3<-c(measure1_halufa1_halufa2$conf.int[1],measure1_halufa1_halufa2$conf.int[2])%>%print
# meanFlow3 <- measure1_halufa1_halufa2$estimate%>%print
# delta3 <- measure1_halufa1_halufa2$conf.int[2]-meanFlow3%>%print
# diuk3 <- delta3/meanFlow3
# print(paste0("The relative accuracy for average waiting time for Current situation is " ,diuk3))


######################################################

# Measure 2 compression original and Halufa 1

# ci4<-c(measure2_original_halufa1$conf.int[1],measure2_original_halufa1$conf.int[2])%>%print
# meanFlow4 <- measure2_original_halufa1$estimate%>%print
# delta4 <- measure2_original_halufa1$conf.int[2]-meanFlow4%>%print
# diuk4 <- delta4/meanFlow4
# print(paste0("The relative accuracy for perctange of disappointment for Current situation is " ,diuk4))


# Measure 2 compression original and Halufa 2

# ci5<-c(measure2_original_halufa2$conf.int[1],measure2_original_halufa2$conf.int[2])%>%print
# meanFlow5 <- measure2_original_halufa2$estimate%>%print
# delta5 <- measure2_original_halufa2$conf.int[2]-meanFlow5%>%print
# diuk5 <- delta5/meanFlow5
# print(paste0("The relative accuracy for perctange of disappointment for Current situation is " ,diuk5))


# Measure 2 compression Halufa 1 and Halufa 2

# ci6<-c(measure2_halufa1_halufa2$conf.int[1],measure2_halufa1_halufa2$conf.int[2])%>%print
# meanFlow6 <- measure2_halufa1_halufa2$estimate%>%print
# delta6 <- measure2_halufa1_halufa2$conf.int[2]-meanFlow6%>%print
# diuk6 <- delta6/meanFlow6
# print(paste0("The relative accuracy for perctange of disappointment for Current situation is " ,diuk6))

######################################################

# Measure 3 compression original and Halufa 1

# ci7<-c(measure3_original_halufa1$conf.int[1],measure3_original_halufa1$conf.int[2])%>%print
# meanFlow7 <- measure3_original_halufa1$estimate%>%print
# delta7 <- measure3_original_halufa1$conf.int[2]-meanFlow7%>%print
# diuk7 <- delta7/meanFlow7
# print(paste0("The relative accuracy for perctange of present in Huppa for Current situation is " ,diuk7))

# Measure 3 compression original and Halufa 2

# ci8<-c(measure3_original_halufa2$conf.int[1],measure3_original_halufa2$conf.int[2])%>%print
# meanFlow8 <- measure3_original_halufa2$estimate%>%print
# delta8 <- measure3_original_halufa2$conf.int[2]-meanFlow8%>%print
# diuk8 <- delta8/meanFlow8
# print(paste0("The relative accuracy for perctange of present in Huppa for Current situation is " ,diuk8))


# Measure 3 compression Halufa 1 and Halufa 2

# ci9<-c(measure3_halufa1_halufa2$conf.int[1],measure3_halufa1_halufa2$conf.int[2])%>%print
# meanFlow9 <- measure3_halufa1_halufa2$estimate%>%print
# delta9 <- measure3_halufa1_halufa2$conf.int[2]-meanFlow9%>%print
# diuk9 <- delta9/meanFlow9
# print(paste0("The relative accuracy for perctange of present in Huppa for Current situation is " ,diuk9))




