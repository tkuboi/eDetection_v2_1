'''
Created on 2014/08/15

@author: toshihirokuboi
'''
from __future__ import division
import math
import sys

inf = 99999999
ineqs = ["lt", "gt"]

class DecisionStump(object):
    '''
    classdocs
    '''
    

    def __init__(self, params):
        '''
        Constructor
        '''
        self.featureName = params.get("featureName")
        self.ineq = params.get("ineq")
        self.thresh = params.get("threshold")
        self.errorRate = params.get("errorRate")
        self.votingWeight = 1
        self.weights = []
        
    def setVotingWeight(self, w):
        self.votingWeight = w
 
    def getVotingWeight(self):
        return self.votingWeight
    
    def getErrorRate(self):
        return self.errorRate
    
    def getThreshold(self):
        return self.thresh
    
    def getIneq(self):
        return self.ineq

    def getFeatureName(self):
        return self.featureName
       
    @staticmethod
    def train(features, fieldName, weights, steps=10):
        '''
        method for training
        takes in list of features with label
        and iterate over all training samples, iter, ineq
        and find thresh, ineq that gives the smallest error rate
        '''
        data = [f[0].get(fieldName) for f in features]
        labels = [f[1] for f in features]
        #print "max={0}, min={1}".format(max(data), min(data))
        _max = float(max(data))
        _min = float(min(data))
        stepSize = (_max - _min) / steps
        initThresh = _min
        error = inf
        threshold = initThresh
        ineq = ineqs[0]
        matches = []
        
        for i in range(0, steps):
            thresh = initThresh + i * stepSize
            for eq in ineqs:
                score = 0
                agreement = []
                for n,feature in enumerate(data):
                    pred = DecisionStump.getLabel(feature, thresh, eq)
                    p, m = DecisionStump.calcScore(pred, labels[n], weights[n])
                    #print "ineq={0}, threshold={1}, prediction={2}, actual={3}, match={4}, score={5}".format(eq, thresh, pred, labels[n], m, p)
                    score += p
                    agreement.append(m)
                
                #print "score = {0}".format(score)
                if (score < error):
                    error = score
                    threshold = thresh
                    ineq = eq
                    matches = []
                    matches.extend(agreement)
                    
        ds = DecisionStump({'ineq':ineq, 'errorRate':error, 'threshold':threshold, 'featureName':fieldName})
        #print matches
        #ds.setWeights(matches, weights)
        return ds, matches
    
    '''
    def setWeights(self, matches, weights):
        for n in range(0,len(matches)):
            weight = 0
            if matches[n] == 0 :
                weight = weights[n] + 1
            else:
                weight = weights[n] - 1
                if weight < 0:
                    weight = 0
            self.weights.append(weight)
    '''
                
    def getWeights(self):
        return self.weights
    
    def classify(self, feature):
        return DecisionStump.getLabel(feature, self.thresh, self.ineq)
                          
    @staticmethod                
    def getLabel(feature, thresh, ineq):
        if ineq == "lt":
            if feature < thresh:
                return 1
            else:
                return 0
        else:
            if feature >= thresh:
                return 1
            else:
                return 0

    @staticmethod                            
    def calcScore(pred, actual, w):
        if (pred == actual):
            return 0, (1,pred)
        else:
            if w == 0:
                return w, (0, 0)
            return w, (0,pred)
        
                
            
    @staticmethod
    def loadTestData():
        feature = [({'field1':1}, 0),
                   ({'field1':10}, 1),
                   ({'field1':2}, 0),
                   ({'field1':9}, 1),
                   ({'field1':3}, 0),
                   ({'field1':8}, 1),
                   ({'field1':4}, 0),
                   ({'field1':7}, 1),
                   ({'field1':5}, 0),
                   ({'field1':6}, 1)
                   ]
        return feature
    
if __name__ == "__main__":
    feature = DecisionStump.loadTestData()
    ds, matches = DecisionStump.train(feature, 'field1', [1,1,1,1,1,1,1,1,1,1])
    print "ineq={0}, threshold={1}".format(ds.getIneq(), ds.getThreshold())
        
        