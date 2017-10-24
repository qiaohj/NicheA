library(dismo)
library(gbm)
setwd("@Target")

presence<-read.table("@Filename", head=T, sep=",")
test_set<-read.table("all.txt", head=T, sep=",")
test_set_bg<-test_set[which(test_set$L==0),]
pseudo_absence<-test_set_bg[sample(nrow(test_set_bg), dim(presence)[1] * 10), ]
train_set<-rbind(presence, pseudo_absence)

gbm_all <- gbm.step(data=train_set, gbm.x = 4:6, gbm.y = 3,
                    family = "bernoulli", tree.complexity = 5,
                    learning.rate = 0.01, bag.fraction = 0.5)


result<-predict.gbm(gbm_all, test_set,
                    n.trees=gbm_all$gbm.call$best.trees, type="response")

test_set$result<-result
write.table(test_set, file="@Result", row.names=F, sep=",")
