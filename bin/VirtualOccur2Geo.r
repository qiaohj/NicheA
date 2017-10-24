##librarys: ggplot2,grid
##parameters: @Target, @datafile, @result
library("ggplot2")
setwd("@Target")
d<-read.csv(file="@datafile", 
  header = T, sep = ",", quote="\"", dec=".",fill = TRUE)
p<-ggplot(data = d,aes(x = Value_In_Tiff)) + 
  geom_histogram(aes(y = ..density..))
 ggsave(plot = p, filename = "@result")