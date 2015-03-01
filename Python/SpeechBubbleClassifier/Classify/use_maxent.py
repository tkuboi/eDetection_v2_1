import csv
import pickle

from nltk.classify import NaiveBayesClassifier
from nltk.classify import accuracy
from scipy import misc

PATH = '/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/'

def selectFeatures(featurelist, keys):
    features = {}
    for key in keys:
        features.update({key:featurelist.get(key)})
    return features

def markImage(filename, minX, minY, maxX, maxY):
    img = misc.imread(PATH + filename, False)
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
        
    misc.imsave(PATH+filename,img)
    
def img2rgb(img):
    #blob = [img.shape[0]][img.shape[1]][3]
    blob = [[[0 for k in xrange(3)] for j in xrange(img.shape[1])] for i in xrange(img.shape[0])]
    for row in range(0, img.shape[0]-1):
        for col in range(0, img.shape[1]-1):
            blob[row][col][0] = img[row][col]
            blob[row][col][1] = img[row][col]
            blob[row][col][2] = img[row][col]
            
    return blob
    
f = open('/Users/Toshihirokuboi/Workspace/eDetection_v2_1/src/featureSet.csv', 'r')
records = csv.reader(f,delimiter=',', quotechar='"')
list = [];
n = 0;
for row in records:
    if (n == 0):
        labels = [label.strip(" ") for label in row];
        print labels
    else:
        data = {}
        i = 0
        features = {}
        for item in row:
            if (i <= 6):
                data.update({labels[i]: (int(item) if item.replace(".","").replace(" ","").isdigit() else item)})
            #elif (i >= 39):
            else:
                features.update({labels[i]: (float(item) if item.replace(".","").replace(" ","").isdigit() else item)})
            i += 1
        data.update({"features":features})
        list.append(data)
        #print list
    n += 1
f.close()
print len(list)
keys = ["enclosed"];
featuresets = [(selectFeatures(item.get("features"),keys), item.get("isBubble")) for (item) in list]
#featuresets = [(item.get("features"), item.get("isBubble")) for (item) in list]
train_set, test_set = featuresets[200:], featuresets[:200]
#print train_set
classifier = NaiveBayesClassifier.train(train_set)
print accuracy(classifier, test_set)
classifier.show_most_informative_features(100)
train_set, test_set = featuresets[:200], featuresets[200:]
classifier.train(train_set)
print accuracy(classifier, test_set)
classifier.show_most_informative_features(100)

f = open('/Users/Toshihirokuboi/Workspace/eDetection_v2_1/src/featureSet2.csv', 'r')
records = csv.reader(f,delimiter=',', quotechar='"')
list = []
n = 0
for row in records:
    data = {}
    i = 0
    features = {}
    if (n > 0):
        for item in row:
            if (i <= 6):
                data.update({labels[i]: item})
            #elif (i >= 39):
            else:
                features.update({labels[i]: (float(item) if item.replace(".","").replace(" ","").isdigit() else item)})
            i += 1
        data.update({"features":features})
        list.append(data)
    n += 1
f.close()
print len(list)
agree = 0; disagree = 0;
for item in list:
    #print item;
    guess = classifier.classify(selectFeatures(item.get("features"),keys))
    #guess = classifier.classify(item.get("features"))
    if (guess == item.get("isBubble")):
        agree += 1
    else:
        disagree += 1
    if (guess == 'true'):
        markImage(item.get('filename'), int(item.get('minX')), int(item.get('minY')), int(item.get('maxX')), int(item.get('maxY')))
    print "%16s; region#=%2s (%3s:%4s,%3s:%4s): guess=%5s, while expert says %s." % (item.get("filename"), item.get("marker"), item.get("minX").strip(" "), item.get("minY").strip(" "), item.get("maxX").strip(" "), item.get("maxY").strip(" "), guess, item.get("isBubble"))
print "agree:%d, disagree:%d, percentage of agree= %d%%" % (agree, disagree, (100*agree/(agree + disagree)))

output = open('myClassifier.pkl', 'wb')
pickle.dump(classifier, output)
output.close()
#inf = open('myClassifier.pkl', 'rb')
#classifier2 = pickle.load(inf)
#classifier2.show_most_informative_features(10)
#inf.close()

    
