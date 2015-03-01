'''
Created on 2014/08/15

@author: toshihirokuboi
'''
from __future__ import division
from random import shuffle
from numpy import *
import math
import sys

from DecisionStump import DecisionStump


class AdaBoost(object):
    '''
    classdocs
    '''


    def __init__(self, params):
        '''
        Constructor
        '''
        self.featureList = params.get('featureList')
        self.classifiers = params.get('classifiers')
        self.votingWeights = params.get('votingWeights')
        
    @staticmethod
    def train(features, steps=10):
        classifiers = []
        #print len(features)
        weights = [10 for n in features]
        errors = []
        featureList = features[0][0].keys()
        #shuffle(featureList)
        for f in featureList:
            print "feature name={0}".format(f)
            ds, matches = DecisionStump.train(features,f, weights, steps)
            #print len(features)
            #print features[0][0]
            AdaBoost.setWeights(weights, matches)
            #AdaBoost.printWeights(weights)
            errors.append(ds.getErrorRate())
            classifiers.append(ds)
            
        _max = max(errors)
        votingWeights = [1 - e / _max for e in errors]
        return AdaBoost({'featureList':featureList, 'classifiers':classifiers, 'votingWeights':votingWeights})
    
    @staticmethod
    def setWeights(weights, matches):
        #print "weights={0}, matches={1}".format(len(weights), len(matches))
        for n,match in enumerate(matches):
            weight = 0
            if match[0] == 0 :
                if match[1] == 0:
                    weight = weights[n] + 1
                else:
                    weight = weights[n] + 1
            else:
                if match[1] == 1:
                    weight = weights[n] - 1
                else:
                    weight = weights[n] - 1
            if weight < 1:
                weight = 1
            weights[n] = weight
            
    @staticmethod
    def printWeights(weights):
        for weight in weights:
            print weight
        
    def classify(self, feature):
        classScore = [0, 0]
        for m,f in enumerate(self.featureList):
            classifier = self.classifiers[m]
            pred = classifier.classify(feature.get(f))
            classScore[pred] += self.votingWeights[m]
            #classScore[pred] += 1
            
        score = classScore[0]
        label = 0
        for i in range(0, len(classScore)):
            if classScore[i] > score:
                label = i
                score = classScore[i]
        return label
    
    
    if __name__ == "__main__":
        '''
        test adaBoost
        '''
        
class AdaBoostAtCascade(AdaBoost):
    @staticmethod
    def train(features, steps=10):
        classifiers = []
        #print len(features)
        weights = [10 for n in features]
        errors = []
        featureList = features[0][0].keys()
        
        for f in featureList:
            #print "feature name={0}".format(f)
            ds, matches = DecisionStump.train(features,f, weights, steps)
            #print len(features)
            #print features[0][0]
            AdaBoostAtCascade.setWeights(weights, matches)
            #AdaBoost.printWeights(weights)
            errors.append(ds.getErrorRate())
            classifiers.append(ds)
            
        _max = max(errors)
        votingWeights = [1 - e / _max for e in errors]
        return AdaBoostAtCascade({'featureList':featureList, 'classifiers':classifiers, 'votingWeights':votingWeights})

    @staticmethod
    def setWeights(weights, matches):
        #print "weights={0}, matches={1}".format(len(weights), len(matches))
        for n,match in enumerate(matches):
            weight = 0
            if match[0] == 1 and match[1] == 0 :
                weight = 0
            else:
                if match[0] == 0 :
                    weight = weights[n] + 1
                else:
                    weight = weights[n] - 1
                if weight < 1:
                    weight = 1
            weights[n] = weight
        
    def classify(self, feature):
        #classScore = [0, 0]
        for m,f in enumerate(self.featureList):
            classifier = self.classifiers[m]
            pred = classifier.classify(feature.get(f))
            if pred == 0:
                return pred
            #classScore[pred] += self.votingWeights[m]
            #classScore[pred] += 1
            
        
        return pred
    

class AdaBoost2(AdaBoost):
    @staticmethod
    def train(features, steps=10):
        classifiers = []
        #print len(features)
        weights = [1 for n in features]
        errors = []
        bestStump = {}
        featureList = features[0][0].keys()
        
        for i in range(0, steps):
            print "iteration: {0}".format(i)
            minError = 99999999
            for f in featureList:
                #print "feature name={0}".format(f)
                ds, matches = DecisionStump.train(features,f, weights)
                error = ds.getErrorRate()
                if error < minError:
                    minError = error
                    bestStump["stump"] = ds
                    bestStump["matches"] = matches
            minError = minError / len(features)
            alpha = float(0.5 * log((1.0-minError) / max(minError, 1e-16)))
            bestStump["alpha"] = alpha
            classifiers.append(bestStump)
            AdaBoost2.setWeights(weights, bestStump["matches"], alpha)
            errors.append(minError)
            #aggErrorRate = sum(errors) / len(features)
            print "error={0}".format(minError)
            if minError == 0:
                break
 
            
        _max = max(errors)
        
        votingWeights = [1 - e / sum(errors) for e in errors]
        return AdaBoost2({'featureList':featureList, 'classifiers':classifiers, 'votingWeights':votingWeights})

    @staticmethod
    def setWeights(weights, matches, alpha):
        
        D = []
        for i,m in enumerate(matches):
            if m[0] == 0:
                label = -1
            else:
                label = 1
            expon = -1 * alpha * label
            D.append(weights[i] * exp(expon))
        _sum = sum(D)
        for i,d in enumerate(D):
            weights[i] = d / _sum
        
        '''
        for n,match in enumerate(matches):
            weight = 0
            if match[0] == 0 :
                if match[1] == 0:
                    weight = weights[n] + 1.3
                else:
                    weight = weights[n] + 1
            else:
                if match[1] == 1:
                    weight = weights[n] - 1
                else:
                    weight = weights[n] - 1
            if weight < 1:
                weight = 1
            weights[n] = weight
        weights = weights / sum(weights)
        '''
        
    def classify(self, feature):
        #preds = []
        classScore = [0, 0]
        aggClassEst = 0
        for i,c in enumerate(self.classifiers):
            f = c["stump"].featureName
            pred = c["stump"].classify(feature.get(f))
            est = pred
            if est == 0:
                est = -1
            aggClassEst += est * c["alpha"]
        if aggClassEst >= 0:
            return 1
        else:
            return 0
        
        '''
        if classScore[1] >= classScore[0]:
            return 1;
        else:
            return 0;
        '''
        
class AdaBoost3(AdaBoost):
    @staticmethod
    def train(features, steps=10):
        classifiers = {}
        #print len(features)
        weights = [1 for n in features]
        errors = []
        bestStump = {}
        featureList = features[0][0].keys()
        
        for i in range(0, steps):
            print "iteration: {0}".format(i)
            minError = 99999999
            for f in featureList:
                #print "feature name={0}".format(f)
                ds, matches = DecisionStump.train(features,f, weights)
                error = ds.getErrorRate()
                if error < minError:
                    minError = error
                    bestStump["stump"] = ds
                    bestStump["matches"] = matches
            minError = minError / len(features)
            alpha = float(0.5 * log((1.0-minError) / max(minError, 1e-16)))
            bestStump["alpha"] = alpha
            classifiers[bestStump["stump"].getFeatureName()]=bestStump
            AdaBoost3.setWeights(weights, bestStump["matches"], alpha)
            #print weights
            errors.append(minError)
            #aggErrorRate = sum(errors) / len(features)
            print "{0}: error={1}".format(bestStump["stump"].getFeatureName(), minError)
            if minError == 0:
                break
 
            
        _max = max(errors)
        
        votingWeights = [1 - e / sum(errors) for e in errors]
        return AdaBoost3({'featureList':featureList, 'classifiers':classifiers, 'votingWeights':votingWeights})

    @staticmethod
    def setWeights(weights, matches, alpha):
        
        D = []
        for i,m in enumerate(matches):
            if m[0] == 0:
                label = -1
            else:
                label = 1
            expon = -1 * alpha * label
            D.append(weights[i] * exp(expon))
        #weights = D / sum(D)
        _sum = sum(D)
        for i,d in enumerate(D):
            weights[i] = d / _sum
        #return weights
        
        
        '''
        for n,match in enumerate(matches):
            weight = 0
            if match[0] == 0 :
                if match[1] == 0:
                    weight = weights[n] + 1.3
                else:
                    weight = weights[n] + 1
            else:
                if match[1] == 1:
                    weight = weights[n] - 1
                else:
                    weight = weights[n] - 1
            if weight < 1:
                weight = 1
            weights[n] = weight
        weights = weights / sum(weights)
        '''
        
    def classify(self, feature):
        #preds = []
        classScore = [0, 0]
        aggClassEst = 0
        for f in self.classifiers.keys():
            #f = c["stump"].featureName
            c = self.classifiers[f]
            pred = c["stump"].classify(feature.get(f))
            est = pred
            if est == 0:
                est = -1
            aggClassEst += est * c["alpha"]
        if aggClassEst >= 0:
            return 1
        else:
            return 0
        
        '''
        if classScore[1] >= classScore[0]:
            return 1;
        else:
            return 0;
        '''
        
class AdaBoost4(AdaBoost):
    @staticmethod
    def train(features, steps=10):
        bestStumps = []
        minError = 99999999
        featureList = features[0][0].keys()
        
        for i in range(steps):
            classifiers = []
            #print len(features)
            weights = [5 for n in features]
            errors = []
            shuffle(featureList)
            for f in featureList:
                #print "feature name={0}".format(f)
                ds, matches = DecisionStump.train(features,f, weights, 15)
                #print len(features)
                #print features[0][0]
                AdaBoost.setWeights(weights, matches)
                #AdaBoost.printWeights(weights)
                errors.append(ds.getErrorRate())
                classifiers.append(ds)
            
            error = sum(errors)
            print("error=",error)
            if error < minError:
                minError = error
                bestStumps = list(classifiers)
                _max = max(errors)
                votingWeights = [1 - e / _max for e in errors]
                
        print("minError=",minError)
        return AdaBoost4({'featureList':featureList, 'classifiers':bestStumps, 'votingWeights':votingWeights})

    def classify(self, feature):
        classScore = [0, 0]
        for n,classifier in enumerate(self.classifiers):
            #classifier = self.classifiers[m]
            pred = classifier.classify(feature[classifier.getFeatureName()])
            classScore[pred] += self.votingWeights[n]
            #classScore[pred] += 1
            
        score = classScore[0]
        label = 0
        for i in range(0, len(classScore)):
            if classScore[i] > score:
                label = i
                score = classScore[i]
        return label
