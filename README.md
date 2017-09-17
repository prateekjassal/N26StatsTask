# N26 Transaction Statistics Assignment
N26's Statistics assignment

## Technology Stack Used

* Java 1.8 with Spring Boot and an embedded Tomcat
* Maven for dependency management
* Logback for logging

## Approaches

I have used two different approaches and both of them work just fine. And I will discuss each of them here -

* Maintain an aggregated statistics singleton that holds the stats for a minute's transactions. Use a sliding window mechanism on an array with 60 slots, everytime a second goes out of range (becomes older than a minute) then that slot is cleared by a cron job. This cron then updates the aggregate statistics singleton by iterating over all the other 59 slots. This will take O(60) time which is constant. Everytime a new transaction comes in, find the index for the second timestamp (by taking mod with 60) and then update that slot's statistics in the array by adding the amount to that slot's sum, incrementing the count, updating minimum and maximum for that second and finding average by using the updated count and sum. In turn, update the aggregated statistics singleton by comparing the new slot statistics with the existing aggregated singleton statistics. This approach will take O(1) time as posting a transaction only requires updating the corresponding second slot in the array and comparing this with the aggregated statistics singleton. The cron job that runs at the start of every second would again take constant time as it iterates over the remaining 59 slots to find the new aggregated statistics. Fetching statistics is again O(1) as it only requires reading the aggregated statistics singletone. Memory wise, an array of 60 slots is being used along with an aggregated statistics singleton so it is constant space again. ArrayBackedStatisticsRepositoryImpl an implementation of IStatisticsRepository uses this approach. I have used 61 slots in code to be able to respond for the last 60 seconds so that I don't end up omitting the oldest second.

* Maintain a segment tree with leaf nodes of 60 slots again. Each slot contains statistics for a second and these 60 slots represent statistics for the last 60 seconds. The root node of the segment tree will contain the aggregated statistics for the minute. A cron job runs at the start of every second to clear up an older second slot and make it available for use. It updates the leaf node first by resetting it's statistics and goes on updating the parents of the leaf node until it reaches the root node and updates it. This operation would take O(height of the tree) time. The changes are bubbled up. For a new incoming transaction, the statistics at the root node are updated first moving on to the appropriate child right until the statistics changes are propagated to the target leaf node. Target leaf node again is calculated based on the current timestamp's rounded off second and where it belongs in the 60 slots. This again takes O(height of the tree) time. Fetching statistics is again O(1) as it requires only reading only the root node's statistics. The height of the tree would be log(60) which is again a constant so both the update operations by the cron and the transaction posting take constant time. This was my inital approach but I thought it was an overkill so I added the first approach as well :). SegmentTreeBackedStatisticsRepositoryImpl an implementation of IStatisticsRepository uses this approach. Again, I have used 61 slots in code to be able to respond for the last 60 seconds so that I don't end up omitting the oldest second.

Both the approaches are interchangeable because of the same interface which they implement. Changing the qualifier from "arrayRepo" to "segRepo" as shown below in the StatisticsServiceImpl class would do so.
``` java
@Qualifier("arrayRepo")
@Autowired private IStatisticsRepository statisticsRepository;
```

``` java
@Qualifier("segRepo")
@Autowired private IStatisticsRepository statisticsRepository;
```

## Tests

I gone with just integration tests instead of unit tests due to time crunch. 

## Building the Project

The project can be built with Maven which will also run the integration tests package with it. The positive integration test has a wait time of 48 seconds in it waiting for older transactions to expire to check statistics. So please do not think the test has stalled. The job logs would be constantly coming in as the job runs every second :)
``` java
mvn clean install
```

After building, an executable jar (target/n26-stats-task-0.0.1-SNAPSHOT.jar) would be available for running the server. The application would run on port 8080 by default. No other arguments need to be passed to the jar.






