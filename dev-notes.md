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


---|---|---
read docs | voc size | available heap
---|---|---
1000 | 20861 | heap: 149910
2000 | 29707 | heap: 166051
3000 | 36659 | heap: 155498
4000 | 42931 | heap: 142503
5000 | 48346 | heap: 131933
6000 | 52840 | heap: 121596
7000 | 57345 | heap: 191891
8000 | 61813 | heap: 192974
9000 | 66148 | heap: 167416
10000 | 70065 | heap: 141434
11000 | 74294 | heap: 196733
12000 | 78415 | heap: 171745


13000, voc.size 82073, heap: 146268
14000, voc.size 86013, heap: 119736
15000, voc.size 89279, heap: 175841
16000, voc.size 92647, heap: 149793
17000, voc.size 95540, heap: 124922
18000, voc.size 98366, heap: 180381
19000, voc.size 101375, heap: 154141
20000, voc.size 104335, heap: 129404
21000, voc.size 107177, heap: 184507
22000, voc.size 110326, heap: 158726
23000, voc.size 113363, heap: 132881
24000, voc.size 116183, heap: 188498
25000, voc.size 119151, heap: 148931
26000, voc.size 121758, heap: 174895
27000, voc.size 124391, heap: 191069
28000, voc.size 127199, heap: 128469
29000, voc.size 129531, heap: 153628
30000, voc.size 131945, heap: 178551
31000, voc.size 134464, heap: 123357
32000, voc.size 138101, heap: 145311
33000, voc.size 140502, heap: 166330
34000, voc.size 142525, heap: 187026
35000, voc.size 144498, heap: 128659
36000, voc.size 146963, heap: 153738
37000, voc.size 149347, heap: 174354
33000, voc.size 140502, heap: 166330
34000, voc.size 142525, heap: 187026
35000, voc.size 144498, heap: 128659
36000, voc.size 146963, heap: 153738
37000, voc.size 149347, heap: 174354
38000, voc.size 151783, heap: 194387
39000, voc.size 154071, heap: 136037
40000, voc.size 156527, heap: 149290
41000, voc.size 158954, heap: 170126
42000, voc.size 161504, heap: 195295
43000, voc.size 163616, heap: 141198
44000, voc.size 165724, heap: 162107
45000, voc.size 167673, heap: 178479
46000, voc.size 169703, heap: 123988
47000, voc.size 171732, heap: 149216
48000, voc.size 173834, heap: 169875
49000, voc.size 176044, heap: 191224
50000, voc.size 178069, heap: 136238


/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/bin/java -Didea.launcher.port=7532 "-Didea.launcher.bin.path=/Applications/IntelliJ IDEA CE.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath "/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/deploy.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/jfxrt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/nashorn.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/javaws.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/jfxswt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/plugin.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/ant-javafx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/javafx-mx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/packager.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/sa-jdi.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/tools.jar:/Users/mmgreiner/Projects/InformationRetrieval/out/production/Scoring:/Users/mmgreiner/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.10.5.jar:/Users/mmgreiner/.ivy2/cache/org.scala-lang/scala-reflect/jars/scala-reflect-2.10.5.jar:/Users/mmgreiner/Projects/InformationRetrieval/libs/tinyir.jar:/Users/mmgreiner/Projects/InformationRetrieval/libs/gson-2.8.0.jar:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar" com.intellij.rt.execution.application.AppMain Classifier
training 50000 documents
	88 secs, or 1.4666666666666666 minutes
vocabulary...
1000, voc.size 20861, heap: 149910
2000, voc.size 29707, heap: 166051
3000, voc.size 36659, heap: 155498
4000, voc.size 42931, heap: 142503
5000, voc.size 48346, heap: 131933
6000, voc.size 52840, heap: 121596
7000, voc.size 57345, heap: 191891
8000, voc.size 61813, heap: 192974
9000, voc.size 66148, heap: 167416
10000, voc.size 70065, heap: 141434
11000, voc.size 74294, heap: 196733
12000, voc.size 78415, heap: 171745
13000, voc.size 82073, heap: 146268
14000, voc.size 86013, heap: 119736
15000, voc.size 89279, heap: 175841
16000, voc.size 92647, heap: 149793
17000, voc.size 95540, heap: 124922
18000, voc.size 98366, heap: 180381
19000, voc.size 101375, heap: 154141
20000, voc.size 104335, heap: 129404
21000, voc.size 107177, heap: 184507
22000, voc.size 110326, heap: 158726
23000, voc.size 113363, heap: 132881
24000, voc.size 116183, heap: 188498
25000, voc.size 119151, heap: 148931
26000, voc.size 121758, heap: 174895
27000, voc.size 124391, heap: 191069
28000, voc.size 127199, heap: 128469
29000, voc.size 129531, heap: 153628
30000, voc.size 131945, heap: 178551
31000, voc.size 134464, heap: 123357
32000, voc.size 138101, heap: 145311
33000, voc.size 140502, heap: 166330
34000, voc.size 142525, heap: 187026
35000, voc.size 144498, heap: 128659
36000, voc.size 146963, heap: 153738
37000, voc.size 149347, heap: 174354
38000, voc.size 151783, heap: 194387
39000, voc.size 154071, heap: 136037
40000, voc.size 156527, heap: 149290
41000, voc.size 158954, heap: 170126
42000, voc.size 161504, heap: 195295
43000, voc.size 163616, heap: 141198
44000, voc.size 165724, heap: 162107
45000, voc.size 167673, heap: 178479
46000, voc.size 169703, heap: 123988
47000, voc.size 171732, heap: 149216
48000, voc.size 173834, heap: 169875
49000, voc.size 176044, heap: 191224
50000, voc.size 178069, heap: 136238
	9064 secs, heap: 136238
classes...
	696 classes
	training 1 documents of class AARCT
	containing 596 tokens
took 20 seconds. Free memory: 141323
	training 13 documents of class ABDBI
	containing 2768 tokens
took 9 seconds. Free memory: 131317
	training 85 documents of class AFGH
	containing 25147 tokens
took 72 seconds. Free memory: 130135
	training 15 documents of class AFRICA
	containing 4158 tokens
took 45 seconds. Free memory: 131823
	training 1 documents of class AJMN
	containing 72 tokens
took 4 seconds. Free memory: 134568
	training 170 documents of class ALB
	containing 51882 tokens
took 128 seconds. Free memory: 128795
	training 113 documents of class ALG
	containing 21514 tokens
took 98 seconds. Free memory: 130411
	training 1 documents of class ANDEAN
	containing 139 tokens
took 6 seconds. Free memory: 134247
	training 1 documents of class ANDO
	containing 437 tokens
took 4 seconds. Free memory: 132513
	training 34 documents of class ANGOL
	containing 7778 tokens
took 16 seconds. Free memory: 131778
	training 7 documents of class ANTA
	containing 949 tokens
took 5 seconds. Free memory: 132438
	training 8 documents of class ARABST
	containing 2311 tokens
took 5 seconds. Free memory: 131622
	training 441 documents of class ARG
	containing 74696 tokens
took 177 seconds. Free memory: 128548
	training 24 documents of class ARMEN
	containing 4685 tokens
took 16 seconds. Free memory: 131274
	training 1 documents of class ARMHG
	containing 217 tokens
took 3 seconds. Free memory: 133205
	training 54 documents of class ASEAN
	containing 16314 tokens
took 33 seconds. Free memory: 131303
	training 27 documents of class ASIA
	containing 10688 tokens
took 22 seconds. Free memory: 131025
	training 264 documents of class AUST
	containing 50809 tokens
took 157 seconds. Free memory: 129133
	training 1759 documents of class AUSTR
	containing 422014 tokens
took 1328 seconds. Free memory: 160908
	training 24 documents of class AZERB
	containing 5190 tokens
took 52 seconds. Free memory: 131312
	training 6 documents of class BAH
	containing 1014 tokens
took 6 seconds. Free memory: 132324
	training 67 documents of class BAHRN
	containing 9319 tokens
took 51 seconds. Free memory: 130888
	training 11 documents of class BALTST
	containing 2407 tokens
took 16 seconds. Free memory: 131380
	training 149 documents of class BANDH
	containing 27932 tokens
took 97 seconds. Free memory: 130095
	training 12 documents of class BARB
	containing 2456 tokens
took 12 seconds. Free memory: 131579
	training 642 documents of class BELG
	containing 126696 tokens
took 422 seconds. Free memory: 125497
	training 6 documents of class BELZ
	containing 1900 tokens
took 6 seconds. Free memory: 132151
	training 7 documents of class BENIN
	containing 1165 tokens
took 4 seconds. Free memory: 131645
	training 6 documents of class BENLUX
	containing 1335 tokens
took 7 seconds. Free memory: 131683
	training 12 documents of class BERM
	containing 2139 tokens
took 9 seconds. Free memory: 131922
	training 2 documents of class BHUTAN
	containing 585 tokens
took 3 seconds. Free memory: 132751
	training 34 documents of class BOL
	containing 6044 tokens
took 21 seconds. Free memory: 131998
	training 19 documents of class BOTS
	containing 3473 tokens
took 9 seconds. Free memory: 131817
	training 683 documents of class BRAZ
	containing 117178 tokens
took 530 seconds. Free memory: 126038
	training 6 documents of class BRUNEI
	containing 1376 tokens
took 6 seconds. Free memory: 132146
	training 181 documents of class BSHZG
	containing 55034 tokens
took 226 seconds. Free memory: 129425
	training 261 documents of class BUL
	containing 63029 tokens
took 351 seconds. Free memory: 128457
	training 65 documents of class BURMA
	containing 18003 tokens

Process finished with exit code 130 (interrupted by signal 2: SIGINT)

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