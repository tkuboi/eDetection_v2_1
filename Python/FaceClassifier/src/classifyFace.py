'''
Created on 2014/07/12

@author: toshihirokuboi
'''

import csv
import sys
import pickle


from random import shuffle
from nltk.classify import NaiveBayesClassifier
from nltk.classify import MaxentClassifier
from nltk.classify import DecisionTreeClassifier
from nltk.classify import accuracy
from scipy import misc
from AdaBoost import AdaBoost, AdaBoostAtCascade, AdaBoost2, AdaBoost3, AdaBoost4



args = sys.argv
if len(args) > 1:
    ALGORITHM = args[1]
else:
    ALGORITHM = "bayes"
if len(args) > 2:
    LEARNING = args[2]
else:
    LEARNING = "non-semi"

#PATH = '/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/image1-2/'
#PATH = '/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/image_0670/'
PATH = '/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/trainingSet/images/'

#KEYS = ["xRange", "yRange", "enclosed", "percentEnclosed", "histV_1", "histV_2", "histV_3", "histV_4", "histV_5", "histV_6", "histV_7", "histV_8", "histV_9", "histV_10", "histH_1", "histH_2", "histH_3", "histH_4", "histH_5", "histH_6", "histH_7", "histH_8", "histH_9", "histH_10"];
#KEYS = ["xRange", "yRange", "distBtwCenters", "enclosed", "percentEnclosed", "histH_1", "histH_2", "histH_3", "histH_4", "histH_5", "histH_6", "histH_7", "histH_8", "histH_9", "histH_10", "histV_1", "histV_2", "histV_3", "histV_4", "histV_5", "histV_6", "histV_7", "histV_8", "histV_9", "histV_10", "histR_1", "histR_2", "histR_3", "histR_4", "histR_5", "histR_6", "histR_7", "histR_8", "histR_9", "histR_10", "histR_11", "histR_12", "histR_13", "histR_14", "histR_15", "histR_16", "histR_17", "histR_18", "histR_19", "histR_20", "histR_21", "histR_22", "histR_23", "histR_24"];
KEYS = []
EXCLUDE = ["marker_1", "isBubble_1", "minX_1", "minY_1", "maxX_1", "maxY_1", "marker_2", "isBubble_2", "minX_2", "minY_2", "maxX_2", "maxY_2", "diffDistBtwCenters", "enumEnclosed"]

def selectFeatures(featurelist, keys, exclude=[]):
    features = {}
    if (len(keys) > 0):
        for key in keys:
            if featurelist.has_key(key):
                features.update({key:featurelist.get(key)})
        return features
    
    elif (len(exclude) > 0):
        for exc in exclude:
            if featurelist.has_key(exc):
                del(featurelist[exc])
        return featurelist

    else:
        return featurelist

def markImage(filename, minX, minY, maxX, maxY):
    img = misc.imread(filename, False)
    #print filename
    #print img.shape
    if (len(img.shape) < 3):
        img = img2rgb(img)
        
    for row in range(minY, maxY):
        img[row][minX][0] = 0
        img[row][minX][1] = 0
        img[row][minX][2] = 255
        
        img[row][maxX][0] = 0
        img[row][maxX][1] = 0
        img[row][maxX][2] = 255
        
    for col in range(minX, maxX):
        img[minY][col][0] = 0
        img[minY][col][1] = 0
        img[minY][col][2] = 255

        img[maxY][col][0] = 0
        img[maxY][col][1] = 0
        img[maxY][col][2] = 255
        
    misc.imsave(filename,img)
    
def img2rgb(img):
    #blob = [img.shape[0]][img.shape[1]][3]
    blob = [[[0 for k in xrange(3)] for j in xrange(img.shape[1])] for i in xrange(img.shape[0])]
    for row in range(0, img.shape[0]-1):
        for col in range(0, img.shape[1]-1):
            blob[row][col][0] = img[row][col]
            blob[row][col][1] = img[row][col]
            blob[row][col][2] = img[row][col]
            
    return blob
    
def trainNBayes(featuresets):
    #idx = 2*len(featuresets) / ratio
    #train_set, test_set = featuresets[idx:], featuresets[:idx]
    train_set = featuresets
    #print train_set
    classifier = NaiveBayesClassifier.train(train_set)
    #print accuracy(classifier, test_set)
    classifier.show_most_informative_features(100)
    #train_set, test_set = featuresets[idx:], featuresets[:idx]
    #classifier.train(train_set)
    #print accuracy(classifier, test_set)
    #classifier.show_most_informative_features(100)
    return classifier
 
def trainMaxent(featuresets):
    #idx = 2*len(featuresets) / ratio
    #train_set, test_set = featuresets[idx:], featuresets[:idx]
    train_set = featuresets
    algo = MaxentClassifier.ALGORITHMS[1]
    #max_iter=20
    classifier = MaxentClassifier.train(train_set, algo, max_iter=3)
    #print accuracy(classifier, test_set)
    classifier.show_most_informative_features(100)
    #train_set, test_set = featuresets[idx:], featuresets[:idx]
    #classifier.train(train_set, algo, max_iter=20)
    #print accuracy(classifier, test_set)
    #classifier.show_most_informative_features(100)
    return classifier

def trainDT(featuresets):
    #idx = 2*len(featuresets) / ratio
    #train_set, test_set = featuresets[idx:], featuresets[:idx]
    train_set = featuresets
    #max_iter=20
    classifier = DecisionTreeClassifier.train(train_set)
    #print accuracy(classifier, test_set)
    #classifier.show_most_informative_features(100)
    #train_set, test_set = featuresets[idx:], featuresets[:idx]
    #classifier.train(train_set, algo, max_iter=20)
    #print accuracy(classifier, test_set)
    #classifier.show_most_informative_features(100)
    return classifier

def trainAdaBoost(featuresets):
    train_set = featuresets
    classifier = AdaBoost.train(train_set,15)
    return classifier

def trainAdaBoostCascade(featuresets):
    train_set = featuresets
    classifier = AdaBoostAtCascade.train(train_set,15)
    return classifier

def trainAdaBoost2(featuresets):
    train_set = featuresets
    classifier = AdaBoost2.train(train_set,10)
    return classifier

def trainAdaBoost3(featuresets):
    train_set = featuresets
    classifier = AdaBoost3.train(train_set,5)
    return classifier

def trainAdaBoost4(featuresets):
    train_set = featuresets
    classifier = AdaBoost4.train(train_set,40)
    return classifier

def importData(filename):    
    f = open(filename, 'r')
    records = csv.reader(f,delimiter=',', quotechar='"')
    lst = [];
    #n = 0;
    for n, row in enumerate(records):
        if (n == 0):
            labels = [label.strip(" ") for label in row];
            #print labels
        else:
            data = {}
            i = 0
            features = {}
            for item in row:
                if (i <= 5):
                    data.update({labels[i]: (int(item.replace(" ","")) if item.replace(".","").replace(" ","").isdigit() else item.replace(" ",""))})
                #elif (i >= 39):
                else:
                    features.update({labels[i]: (float(item.replace(" ","")) if item.replace(".","").replace(" ","").isdigit() else item.replace(" ",""))})
                #print item
                i += 1
            data.update({"features":features})
            #print data
            lst.append(data)
            #print list
        #n += 1
    f.close()
    #print len(lst)
    return lst;

def run(lst, rTrain=2, rTest=1, weight=2, semi=0):
    newdata = []
    classifier = None;
    a = p =r = f = 0
    ratio = rTrain + rTest;
    shuffle(lst)
    if rTrain > 0:
        featuresets = [(selectFeatures(item.get("features"),KEYS, EXCLUDE), item.get("isEyes")) for (item) in lst[:rTrain*len(lst)/ratio]]
        #featuresets = [(item.get("features"), item.get("isEyes")) for (item) in list]
        if (ALGORITHM == "maxent"):
            classifier = trainMaxent(featuresets)
        elif (ALGORITHM == "dt"):
            classifier = trainDT(featuresets)
        elif (ALGORITHM == "ada"):
            classifier = trainAdaBoost(featuresets)
        elif (ALGORITHM == "casc"):
            classifier = trainAdaBoostCascade(featuresets)
        elif (ALGORITHM == "ada2"):
            classifier = trainAdaBoost2(featuresets)
        elif (ALGORITHM == "ada3"):
            classifier = trainAdaBoost3(featuresets)
        elif (ALGORITHM == "ada4"):
            classifier = trainAdaBoost4(featuresets)
        else:
            classifier = trainNBayes(featuresets)
        for i in range(0,weight):
            newdata.extend(lst[:rTrain*len(lst)/ratio])
    
    if rTest > 0:
        agree = 0; disagree = 0; tp = 0; fp = 0; tn = 0; fn = 0;
        for item in lst[rTrain*len(lst)/ratio:]:
            #print item;
            guess = classifier.classify(selectFeatures(item.get("features"),KEYS, EXCLUDE))
            #guess = classifier.classify(item.get("features"))
            if (guess == item.get("isEyes")):
                agree += 1
                if (guess == 1):
                    tp += 1
                else:
                    tn += 1
                if (semi != 1):
                    newdata.append(item)
            else:
                disagree += 1
                if (guess == 1):
                    fp += 1
                else:
                    fn += 1
                if (semi != 1):
                    for w in range(0,3):
                        newdata.append(item)

                    
            if (guess == '1' or guess == 1):
                markImage(item.get('filename'), int(item.get('minX')), int(item.get('minY')), int(item.get('maxX')), int(item.get('maxY')))
            print "%16s; region#=%2s (%3s:%4s,%3s:%4s): guess=%5s, while expert says %s." % (item.get("filename"), item.get("marker"), item.get("minX"), item.get("minY"), item.get("maxX"), item.get("maxY"), guess, item.get("isEyes"))
            if (semi == 1):
                item["isEyes"] = guess.strip()
                newdata.append(item)
                
        a = 0.1*(1000*agree/(agree + disagree))
        if (tp+fp > 0):
            p = 0.1*(1000*tp/(tp+fp))
        else:
            p = 0
        if (tp+fn > 0):
            r = 0.1*(1000*tp/(tp+fn))
        else:
            r = 0
        if (p+r > 0):
            f = 2*p*r/(p+r)
        else:
            f = 0   
        print "agree:%d, disagree:%d, percentage of agree= %f%%" % (agree, disagree, a)
        print "TP={0}, FP={1}, TN={2}, FN={3}, Precision={4}, Recall={5}".format(tp, fp, tn, fn, p, r)
        print len(newdata)
    return classifier, newdata, [a,p,r,f]

def test(classifier, lst):
    agree = 0; disagree = 0; tp = 0; fp = 0; tn = 0; fn = 0;
    labels = []
    for item in lst:
        #print item;
        guess = classifier.classify(selectFeatures(item.get("features"),KEYS))
        #guess = classifier.classify(item.get("features"))
        if (guess == item.get("isEyes")):
            agree += 1
            if (guess == 1):
                tp += 1
            else:
                tn += 1
        else:
            disagree += 1
            if (guess == 1):
                fp += 1
            else:
                fn += 1
        if (guess == 'true' or guess == ' true'):
            markImage(item.get('filename'), int(item.get('minX')), int(item.get('minY')), int(item.get('maxX')), int(item.get('maxY')))
        labels.append(item.get('filename') + "," + str(item.get("marker")) + "," + str(guess))    
        print "%16s; region#=%2s (%3s:%4s,%3s:%4s): guess=%5s, while expert says %s." % (item.get("filename"), item.get("marker"), item.get("minX"), item.get("minY"), item.get("maxX"), item.get("maxY"), guess, item.get("isEyes"))
    a = 0.1*(1000*agree/(agree + disagree))
    if (tp+fp > 0):
        p = 0.1*(1000*tp/(tp+fp))
    else:
        p = 0
    if (tp+fn > 0):
        r = 0.1*(1000*tp/(tp+fn))
    else:
        r = 0
    if (p+r > 0):
        f = 2*p*r/(p+r)
    else:
        f = 0   
    print "agree:%d, disagree:%d, percentage of agree= %f%%" % (agree, disagree, 0.1*(1000*agree/(agree + disagree)))
    print "TP={0}, FP={1}, TN={2}, FN={3}, Precision={4}, Recall={5}".format(tp, fp, tn, fn, p, r)
    return [a,p,r,f], labels
    

if __name__ == "__main__":
    #learning = "non-semi"
    result = [0,0,0,0]
    lst = importData('/Users/Toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesInfo1.txt')
    num_trial = 10
    if LEARNING == "non-semi":
        lst2 = importData('/Users/Toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesInfo2.txt')
        lst.extend(lst2)
        for i in range(0,num_trial):
            classifier, newdata, res = run(lst, 2, 1, 1)
            for n in range(0,0):
                classifier, newdata, res = run(newdata, 2, 1, 1)
            for j in range(0,4):
                result[j] += res[j]
    elif LEARNING == "semi":
        for i in range(0,num_trial):
            classifier, newdata, res = run(lst, 2, 1, 2, 0)
            classifier, newdata, res = run(newdata, 1, 0, 0, 1)
            lst2 = importData('/Users/Toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesInfo2.txt')
            res, labels = test(classifier, lst2)
            for j in range(0,4):
                result[j] += res[j]
    elif LEARNING == "ensemble":
        classifier, newdata, res = run(lst, 1, 0, 0, 1)
        lst2 = importData('/Users/Toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesInfo2.txt')
        res, labels = test(classifier, lst2)
        output = open('labels_py.csv', 'w')
        for label in labels:
            output.write(label + "\n")
        output.close()
        #classifier, newdata = run(lst2, 0, 3, 2)
    print "{0}: Accuracy={1}, Precision={2}, Recall={3}, F={4}".format(LEARNING, result[0]/num_trial, result[1]/num_trial, result[2]/num_trial, result[3]/num_trial)
    output = open('myClassifier.pkl', 'wb')
    pickle.dump(classifier, output)
    output.close()
    
    #inf = open('myClassifier.pkl', 'rb')
    #classifier2 = pickle.load(inf)
    #classifier2.show_most_informative_features(10)
    #inf.close()

