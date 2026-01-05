# A Java solution for Challenge#14 on StackOverFlow
https://stackoverflow.com/beta/challenges/79838396/challenge-14-signal-from-noise


## Description
### Introduction
Here is my solution with Java to challenge#14

The challenge involves developing a solution capable of finding valid signals withing sequences of values disrupted by some noise signal.

The solution is quite complex, yet its executable front (Main class) is elegant and simple to understand.

### The algorithm
* The original sequence is sorted using the natural order of the numbers (ascending)
* The original sequence is brokered into smaller sequences, obtained by walking over every single element - a counter over this original sequence is kept
* The brokered smaller sequences are then walked over with a second separate counter; at every walk, the second counter starts from the position pointed by the original counter; this inner walker service extract sequences that follow the defined structure of signals eligible for validity
* Every such extracted sequence maintains the chain of valid values, the duplicate and the missing value
* To discriminate if a value is valid, duplicate or missing, the extractor service relies on the distance from the last valid value : 0 for duplicates, 1 for corrects, 2 for missings
* The distance is the absolute value of the difference between current value and last valid signal
* When the extractor service finds a distance bigger then 2, it determines that the sequence ended and returns the extracted object to the brokering service (the one that keeps a counter on the original sequence)
* The broker keeps walking and calling the extractor at every step, till it reaches the end of the original sequence
* The extracted sequences are finally returned to a service which compares them on the basis of the length of the chain of signals, yielding the largest as the valid result
* The sequence elected as valid, is turned into a printable resulting object with the start value, end value, duplicate, missing and the noise
* The noise is obtained by simply taking the original sequence and removing from it the chain of valid values + the duplicate
* The front service receives such object, and calculated the sum of start values and end values, then prints every such object with the final total sum at the very end

### The structure
* The Main class, serving as a front of the application; reads input file and prints result on standard-out
* A ResultingSum class, that peeks into the flow of the extracted valid signals per every sequence, calculating the final sum
* The ‘core’ of the application in an homonymous package, with a class ‘SequenceAnalysis’, used as container for valid signals per sequence, and with 2 sub-packages ‘logic’ and ‘exceptions’
* The package ‘logic’ contains the services described in chapter ‘The Algorithm’
    * ‘Constants’ contains the distances used for discriminating components of a signal
    * ‘ExtractedSequence’ contains the result of the extractor service, eligible to become ‘SequenceAnalysis’
    * ‘ISequenceAnalyzer’ is just a simple interface to decouple implementations in this package, from what will be used just outside of it
    * ‘SequenceAnalyzer’ is the implementation of the interface used outside out the package, which delegates to the broker and extractor service the original sequence, and then compares all eligible ‘ExtractedSequence’ to have the final ‘SequenceAnalysis’ (returned outside of the package)
    * ‘SequenceAnalyzerFactory’ is just another simple interface to decouple construction of the service from access to real constructors
    * ‘ExtractorService’ has 2 methods doing brokering of the original sequence and extractions of eligible sequences (‘ExtractedSequence’). It works mainly through recursive calls of the broker and extractor methods.
* The package ‘exceptions’ contains java-extensions (directly or indirectly) of RuntimeExceptions
    * ‘SequenceStartedException’ used to manage the case where no previous signals has still been extracted, used when discriminating the first value of sequence to know that the signal is to be assumed as correct
    * ‘SequenceFinishedException’ an abstract class used to define when a walk, from the broker or the extractor, is finished
    * ‘ExtractedSequenceFinishedException’, extends ‘SequenceFinishedException’, defines the end of walk from the extractor
    * ‘OriginalSequenceFinishedException’, extends ‘SequenceFinishedException’, defines the end of walk from the broker
* A utility class ‘StringContainingArray’, used by ‘Main’ when reading input files


## Result
### Sum
The final sum I get to is **2293 !**


## More
### Notes
The code was tested against additional sequences, that you can find in the resources of this project.

### External libs
I rely on the framework apache-commons, to help the development
