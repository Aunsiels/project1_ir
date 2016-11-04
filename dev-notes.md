# Development notes

M. Greiner

## Overview
These are notes taking during the development of the Exercise 1 of the class Information retrieval.

## Vocabulary size
We are training on 50'000 documents. 
When reading the documents, we need to assemble the total vocabulary.

These are the results of reading all those files:

training 50000 documents
	88 secs, or 1.4666666666666666 minutes
vocabulary...


## Test table
 | Tables        | Are           | Cool  |
 |-------------|-------------|-----|
 | col 3 is      | right-aligned | $1600 |
 | col 2 is      | centered      |   $12 |
 | zebra stripes | are neat      |    $1 |

##The real table

Reading the documents and observing the increase in vocabulary size.
This is for the original tokenizer, no stemming, no stop words or number replacement.

| read docs | voc size | available heap |
|------:|-------:|-------:|
| 1000 | 20861 | 149910 |
|2000 | 29707 | 166051 |
|3000 | 36659 | 155498 |
|4000 | 42931 | 142503 |
|5000 | 48346 | 131933 |
|6000 | 52840 | 121596 |
|7000 | 57345 | 191891 |
|8000 | 61813 | 192974 |
|9000 | 66148 | 167416 |
|10000 | 70065 | 141434 |
|11000 | 74294 | 196733 |
|12000 | 78415 | 171745 |
| 13000  |   82073 |  146268  |
| 14000  |   86013 |  119736  |
| 15000  |   89279 |  175841  |
| 16000  |   92647 |  149793  |
| 17000  |   95540 |  124922  |
| 18000  |   98366 |  180381  |
| 19000  |   101375 |  154141  |
| 20000  |   104335 |  129404  |
| 21000  |   107177 |  184507  |
| 22000  |   110326 |  158726  |
| 23000  |   113363 |  132881  |
| 24000  |   116183 |  188498  |
| 25000  |   119151 |  148931  |
| 26000  |   121758 |  174895  |
| 27000  |   124391 |  191069  |
| 28000  |   127199 |  128469  |
| 29000  |   129531 |  153628  |
| 30000  |   131945 |  178551  |
| 31000  |   134464 |  123357  |
| 32000  |   138101 |  145311  |
| 33000  |   140502 |  166330  |
| 34000  |   142525 |  187026  |
| 35000  |   144498 |  128659  |
| 36000  |   146963 |  153738  |
| 37000  |   149347 |  174354  |
| 33000  |   140502 |  166330  |
| 34000  |   142525 |  187026  |
| 35000  |   144498 |  128659  |
| 36000  |   146963 |  153738  |
| 37000  |   149347 |  174354  |
| 38000  |   151783 |  194387  |
| 39000  |   154071 |  136037  |
| 40000  |   156527 |  149290  |
| 41000  |   158954 |  170126  |
| 42000  |   161504 |  195295  |
| 43000  |   163616 |  141198  |
| 44000  |   165724 |  162107  |
| 45000  |   167673 |  178479  |
| 46000  |   169703 |  123988  |
| 47000  |   171732 |  149216  |
| 48000  |   173834 |  169875  |
| 49000  |   176044 |  191224  |
| 50000  |   178069 |  136238  |




## Changing SBT Heap size

I found this [hint](http://stackoverflow.com/questions/15280839/how-to-set-heap-size-for-sbt)

In file `/usr/local/etc/sbtopts`, change memory:

    # set memory options
    #
    #-mem   <integer>
    -mem 2048

But I think this is only for the build process, not for execution.

For execution, I set the following in the Run --> Configuration --> VM Options: `-Xmx4g`

## No Stemming

time smart: 36.258620073
 tokens nof 1420981, vocab set 57523
 raw    nof 1420981, vocab set 61813
done

## Stemming

time smart: 49.936969484
 tokens nof 1420981, vocab set 45873
 raw    nof 1420981, vocab set 61813
done

### Stemming, 20'000 elements

starting slicing with 20000 elements. Max Heap 3817.865216 MB, free heap 53.884272 MB

- completed slicing in 0.67798829 secs
- starting tokenizing
- flatMaps.size = 3620595, in 177.400657962 secs
- vocab1 in 4.806296404 secs
- vocab2 in 3.107011141 secs
- time smart: 185.3271967 secs
- tokens nof 3620595, vocab set 79407
- raw    nof 3620595, vocab set 104335
- done

