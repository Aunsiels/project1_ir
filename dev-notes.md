# Development notes

M. Greiner

## Overview
These are notes taking during the development of the Exercise 1 of the class Information retrieval.

## Vocabulary size
We are training on 50'000 documents. 
When reading the documents, we need to assemble the total vocabulary.

### Vocabulary size and documents

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



## Changing Heap size

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


starting slicing with 30000 elements. Max Heap 3817.865216 MB, free heap 53.880976 MB
- completed slicing in 0.972992256 secs
- starting tokenizing
    -    flatMaps.size = 5500305, in 363.821393225 secs
    -   vocab1 in 18.966848393 secs
    -   vocab2 in 13.264810666 secs
- time smart: 396.086705677 secs
    - tokens nof 5500305, vocab set 101535
    - raw    nof 5500305, vocab set 131945
- done

## Tokenizer
/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/bin/java -Xmx4g -Didea.launcher.port=7534 "-Didea.launcher.bin.path=/Applications/IntelliJ IDEA CE.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath "/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/deploy.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/jfxrt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/nashorn.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/javaws.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/jfxswt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/plugin.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/ant-javafx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/javafx-mx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/packager.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/sa-jdi.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/lib/tools.jar:/Users/mmgreiner/Projects/InformationRetrieval/out/production/Scoring:/Users/mmgreiner/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.10.5.jar:/Users/mmgreiner/.ivy2/cache/org.scala-lang/scala-reflect/jars/scala-reflect-2.10.5.jar:/Users/mmgreiner/Projects/InformationRetrieval/libs/gson-2.8.0.jar:/Users/mmgreiner/Projects/InformationRetrieval/libs/tinyir.jar:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar" com.intellij.rt.execution.application.AppMain RCVStreamSmart
starting slicing with 50000 elements. Max Heap 3817.865216 MB, free heap 53.945744 MB
completed slicing in 0.602388116 secs
starting tokenizing
0 USA: Braxton Cty, W.Va., $28 mln waste bonds repriced.: 0.00159203
total time 0.225105528

| read docs | title | total time |
|------:|-------|-------:|
| 1000 |  ISRAEL: Israeli shares shed early gains to close down.: | 8.816039933 |
| 2000 |  UK: UK retailers forecast steady Q4 sales growth - D&B.: | 15.000100225 |
| 3000 |  USA: NYCE cotton settles up, drifts in recent range.: | 21.575783742 |
| 4000 |  AUSTRALIA: RTRS-Pasminco sees Budel licence by yr-end.: | 27.02706512 |
| 5000 |  USA: NY physical coffee - spot supply crunch persists.: | 33.190020936 |
| 6000 |  SWEDEN: ABB wins 600 mln SKR Filipino power order.: | 40.561649198 |
| 7000 |  UK: Gutfreund backs Optima's new Universal Bond fund.: | 44.333301292 |
| 8000 |  BRAZIL: Brazil's Algar, 3 foreign firms plan cellular bid.: | 48.299618444 |
| 9000 |  SPAIN: Reform seen key to jobs, not Spain business tax cut.: | 55.524369884 |
| 10000 |  TURKEY: Turkey sacks military officers in "Islamist purge".: | 59.74818879 |
| 11000 |  ESTONIA: Estonia's Union bank sets 15 mln mark bond.: | 65.1529771 |
| 12000 |  JAPAN: Next G7 summit may discuss global finance - Japan PM.: | 73.637767212 |
| 13000 |  USA: Democrats may try to delay House vote on Gingrich.: | 80.386836752 |
| 14000 |  EGYPT: Cairene bank proposing pound bonds.: | 85.325097238 |
| 15000 |  UK: Metals Outlook-Rally set to resume, funds mobilise.: | 89.987844062 |
| 16000 |  SOUTH AFRICA: Rainbow flies on hopes for local chickens.: | 94.885354382 |
| 17000 |  UK: Kuwait says ready to counter possible Iraq threat.: | 105.034659653 |
| 18000 |  USA: NHL ICE HOCKEY-STANDINGS AFTER FRIDAY'S GAMES.: | 109.865258578 |
| 19000 |  JAPAN: Square sees 97/98 sales of 8.7 mln software units.: | 123.425899965 |
| 20000 |  USA: U.S. sanctions against Colombia feared.: | 128.872923333 |
| 21000 |  SPAIN: SOCCER-MATCHES IN THE SPANISH LEAGUE CHAMPIONSHIP.: | 133.561204106 |
| 22000 |  RUSSIA: Russian shares nudge up, but not much, on Yeltsin.: | 138.900109583 |
| 23000 |  JAPAN: RESEARCH ALERT - Ryohin Keikaku rated.: | 161.513456023 |
| 24000 |  JAPAN: Daiwa, Nikko, Yamaichi seen to cut forecasts-paper.: | 166.244569123 |
| 25000 |  BELARUS: Russia criticises Belarus, U.S. recalls Minsk envoy.: | 176.172941509 |
| 26000 |  LATVIA: (RPT) LATVIA-MINISTER FOR LPA BOARD CHANGES.: | 210.552564892 |
| 27000 |  USA: Texas/w Okla feedlot cattle trade at $72 - USDA.: | 216.909214525 |
| 28000 |  MEXICO: Mexican crude ports remain shut with ships waiting.: | 222.271519365 |
| 29000 |  USA: New Century Financial Corp files for IPO.: | 261.110981966 |
| 30000 |  SOUTH AFRICA: S.Africa's NewFarmers places 41 mln rand shares.: | 281.38722771 |
| 31000 |  INDIA: PRESS DIGEST - Indian newspapers - May 1.: | 289.146981585 |
| 32000 |  HONG KONG: HK blue chips close modestly up, H-shares soar.: | 297.429867712 |
| 33000 |  USA: Blue-chip stocks buoyed by good inflation news.: | 304.377487723 |
| 34000 |  UK: BADMINTON-SUDIRMAN CUP WORLD TEAM CHAMPIONSHIPS RESULTS.: | 310.681409602 |
| 35000 |  MEXICO: Mexico's new Colosio prosecutor to start all over.: | 394.169898945 |
| 36000 |  CANADA: Bundesbank's Hartmann sees 1.70 DM as "adequate".: | 597.474653878 |


Process finished with exit code 130 (interrupted by signal 2: SIGINT)

Starts degradign quite fast after 30'000 read documents.

## Development environment

On OSX, when a directory is zipped, the resource files are also added.
Have to use:

    $ zip -rX hallo.zip directory
    
