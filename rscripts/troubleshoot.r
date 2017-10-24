library("ggplot2")
library("plyr")
library("scales")
library("grid")
setwd("@Target")
df <- data.frame(x=1:12, y=20:31, z=1:12)
p <- ggplot(df, aes(x=x, y=y)) +
  geom_point() +
  geom_segment(aes(xend=c(tail(x, n=-1), NA), yend=c(tail(y, n=-1), NA)),
               arrow=arrow(length=unit(0.3,"cm")))
ggsave(plot = p, filename = "fig1.png")


# create factors with value labels 
mtcars$gear <- factor(mtcars$gear,levels=c(3,4,5),
  	labels=c("3gears","4gears","5gears")) 
mtcars$am <- factor(mtcars$am,levels=c(0,1),
  	labels=c("Automatic","Manual")) 
mtcars$cyl <- factor(mtcars$cyl,levels=c(4,6,8),
   labels=c("4cyl","6cyl","8cyl")) 

# Kernel density plots for mpg
# grouped by number of gears (indicated by color)
p<-qplot(mpg, data=mtcars, geom="density", fill=gear, alpha=I(.5), 
   main="Distribution of Gas Milage", xlab="Miles Per Gallon", 
   ylab="Density")
ggsave(plot = p, filename = "fig2.png")
