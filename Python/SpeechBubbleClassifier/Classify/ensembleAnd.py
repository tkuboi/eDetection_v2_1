import csv, sys

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
                if (i <= 6):
                    data.update({labels[i]: (int(item.replace(" ","")) if item.replace(".","").replace(" ","").isdigit() else item.replace(" ",""))})
                #elif (i >= 39):
                else:
                    features.update({labels[i]: (float(item.replace(" ","")) if item.replace(".","").replace(" ","").isdigit() else item.replace(" ",""))})
                #print item
                i += 1
            data.update({"features":features})
            lst.append(data)
            #print list
        #n += 1
    f.close()
    #print len(lst)
    return lst;

def importLabel(filename):    
    f = open(filename, 'r')
    records = csv.reader(f,delimiter=',', quotechar='"')
    lst = [];
    for record in records:
        row = [data.strip(" ") for data in record];
        lst.append(row)
    f.close()
    return lst

data = importData('/Users/Toshihirokuboi/Workspace/eDetection_v2_1/src/featureSet3.csv')
labels_py = importLabel('/Users/Toshihirokuboi/Workspace/SpeechBubbleClassifier/Classify/labels_py.csv')
labels_svm = importData('/Users/Toshihirokuboi/Workspace/eDetection_v2_1/labels_svm.csv')

print len(labels_py)
print labels_py[399]
print labels_py[400]
print len(labels_svm)
print labels_svm[399]
#print data[399]
#print data[400]

agree = 0; disagree = 0; tp = 0; fp = 0; tn = 0; fn = 0; pos = 0; neg = 0;

for i, rec in enumerate(data):
    if rec.get('filename') != labels_py[i][0] or rec.get('marker') != labels_py[i][1] :
        print "error! Record mismatch!"
        print rec.get('filename') + "."
        print labels_py[i][0] + "."
        print labels_svm[i].get('filename') 
        print rec.get('marker')
        print labels_py[i][1]
        print labels_svm[i].get('marker')
        #exit()
    print rec.get("isBubble")
    if rec.get("isBubble") == "true":
        pos += 1
    else:
        neg += 1
    print "pos={0}, neg={1}".format(pos,neg)
        
    if (labels_svm[i].get('isBubble') == labels_py[i][2]):
        guess = labels_py[i][2]
    else:
        guess = "false"
    
    if (guess == rec.get("isBubble")):
        agree += 1
        if (rec.get("isBubble") == "true"):
            tp += 1
        else:
            tn += 1
    else:
        disagree += 1
        if (rec.get("isBubble") == "false"):
            fp += 1
        else:
            fn += 1
                
    print "%16s; region#=%2s (%3s:%4s,%3s:%4s): guess=%5s, while expert says %s." % (rec.get("filename"), rec.get("marker"), rec.get("minX"), rec.get("minY"), rec.get("maxX"), rec.get("maxY"), guess, rec.get("isBubble"))
    print "TP={0}, FP={1}, TN={2}, FN={3}".format(tp, fp, tn, fn)
a = 0.1*(1000*agree/(agree + disagree))
p = 0.1*(1000*tp/(tp+fp))
r = 0.1*(1000*tp/(tp+fn))
f = 2*p*r/(p+r)    
print "agree:%d, disagree:%d, percentage of agree= %f%%" % (agree, disagree, 0.1*(1000*agree/(agree + disagree)))
print "TP={0}, FP={1}, TN={2}, FN={3}, Precision={4}, Recall={5}, F={6}".format(tp, fp, tn, fn, 0.1*(1000*tp/(tp+fp)), 0.1*(1000*tp/(tp+fn)), f)
print "Pos={0}, Neg={1}".format(pos, neg)

