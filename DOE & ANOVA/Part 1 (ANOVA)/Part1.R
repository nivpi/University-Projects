# install.packages("nortest")
# install.packages("car")
# install.packages("outliers")
# install.packages("ggpubr")
library("nortest")
library("car")
library("outliers")
library("ggpubr")


# Loading the data
strength <- as.vector(as.matrix(read.delim("data.txt")))
facility <- rep(1:5, each=2)
aluminum <- rep(c("0.12%", "0.14%", "0.16%", "0.18%", "0.20%"), each=10)

# Organizing the data
data <- data.frame(aluminum, facility, strength)
rm(strength, facility, aluminum)
data$facility <- factor(data$facility)
data$aluminum <- factor(data$aluminum)

# Validating the table design
str(data)
table(data$aluminum, data$facility)


# Running a Randomized Complete Block ANOVA test
model <- aov(strength ~ aluminum + facility, data)
summary(model)

# Facility wasn't significant
# Running a standard one-way ANOVA
model2 <- aov(strength ~ aluminum, data)
summary(model2)


# Normality Assumption
ggqqplot(model2$residuals)
shapiro.test(model2$residuals)

# Homogeneity of Variance Assumption
plot(model2, 1)
cochran.test(strength ~ aluminum, data)

# Independence Assumption
plot(model2$residuals)
abline(0,0, col="red")



# Checking factor levels viable for post-hoc analysis

mse <- summary(model2)[[1]]["Residuals", "Mean Sq"]
df <- summary(model2)[[1]]["Residuals", "Df"]
k <- 5
n <- 10

hsd <- qtukey(0.05, k, df, lower.tail = FALSE) * sqrt(mse/n)
hsd
tukey_comparisons <- TukeyHSD(model2)
tukey_comparisons
