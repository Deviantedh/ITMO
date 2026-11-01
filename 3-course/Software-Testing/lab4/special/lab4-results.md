# Lab 4 JMeter Results

## Environment

- JMeter: Apache JMeter 5.6.3
- Target service: `http://stload.se.ifmo.ru:8080`
- Token: `495386098`
- User: `-2105023345`
- Required load: 12 users * 20 requests/min = 240 requests/min
- Max allowed response time from task: 570 ms

## Useful Commands

Start local SSH tunnel from Mac to the internal service:

```bash
ssh -p 2222 -N -L 8080:stload.se.ifmo.ru:8080 s368598@helios.cs.ifmo.ru
```

Check the local tunnel:

```bash
curl 'http://localhost:8080?token=495386098&user=-2105023345&config=3'
```

Check the service directly from Helios:

```bash
curl 'http://stload.se.ifmo.ru:8080?token=495386098&user=-2105023345&config=3'
```

Run JMeter on Helios in non-GUI mode:

```bash
cd ~/tpo4
HEAP="-Xms128m -Xmx512m" ./apache-jmeter-5.6.3/bin/jmeter \
  -n \
  -t lab4-load-test-helios.jmx \
  -l results-config3-helios.jtl \
  -j jmeter-config3-helios.log
```

Copy raw JMeter results from Helios to the local project:

```bash
scp -P 2222 s368598@helios.cs.ifmo.ru:~/tpo4/results-config3-helios.jtl .
```

## Load Test Results From Mac Through SSH Tunnel

| Config | Cost | Samples | Avg, ms | Median, ms | 90%, ms | 95%, ms | 99%, ms | Min, ms | Max, ms | Error % | Throughput |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 1 | $2700 | 688 | 960 | 815 | 1193 | 1494 | 2803 | 722 | 3596 | 0.00% | 3.8/sec |
| 2 | $3000 | 713 | 654 | 565 | 844 | 983 | 2125 | 521 | 2924 | 0.00% | 4.0/sec |
| 3 | $3000 | 710 | 474 | 382 | 689 | 892 | 1537 | 322 | 1840 | 0.00% | 3.9/sec |

## Load Test Results Directly From Helios

| Config | Cost | Samples | Avg, ms | Median, ms | 90%, ms | 95%, ms | 99%, ms | Min, ms | Max, ms | Error % | Throughput | HTTP codes |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---|
| 1 | $2700 | 548 | 3278 | 2994 | 4380 | 4493 | 4761 | 2587 | 4945 | 0.00% | 3.00/sec | 200 |
| 2 | $3000 | 562 | 3187 | 2871 | 4179 | 4361 | 4425 | 2419 | 4429 | 0.00% | 3.08/sec | 200 |
| 3 | $3000 | 717 | 381 | 334 | 337 | 632 | 1395 | 304 | 2128 | 0.00% | 3.97/sec | 200 |

## Clean Sequential Helios Run

Order: `config=1 -> 60s cooldown -> config=2 -> 60s cooldown -> config=3`.

| Config | Cost | Samples | Avg, ms | Median, ms | 90%, ms | 95%, ms | 99%, ms | Min, ms | Max, ms | Error % | Throughput | HTTP codes |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---|
| 1 | $2700 | 707 | 735 | 734 | 736 | 737 | 738 | 704 | 2461 | 0.00% | 3.91/sec | 200 |
| 2 | $3000 | 697 | 1207 | 535 | 3133 | 3667 | 4249 | 504 | 4467 | 0.00% | 3.82/sec | 200 |
| 3 | $3000 | 616 | 2659 | 2519 | 3171 | 3451 | 4441 | 1600 | 8453 | 0.00% | 3.40/sec | 200 |

## Isolated Helios Reruns

These runs were started after checking that no JMeter process was active on Helios and no local SSH tunnel test was running.

| Config | Cost | Samples | Avg, ms | Median, ms | 90%, ms | 95%, ms | 99%, ms | Min, ms | Max, ms | Error % | Throughput | HTTP codes |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---|
| 2 | $3000 | 603 | 2200 | 2409 | 3685 | 3885 | 4830 | 524 | 4833 | 0.00% | 3.30/sec | 200 |
| 2 clean fresh | $3000 | 501 | 3197 | 2981 | 6102 | 7023 | 8905 | 513 | 10447 | 0.00% | 2.65/sec | 200 |

## Local Stress Test For Config 3

Stress plan: `lab4-stress-local.jmx`, target through local SSH tunnel `localhost:8080`, ramp-up 30s, duration 120s.

| Users | Target req/min | Samples | Avg, ms | Median, ms | 90%, ms | 95%, ms | 99%, ms | Min, ms | Max, ms | Error % | Throughput |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 12 | 240 | 472 | 824 | 576 | 1350 | 2702 | 4845 | 325 | 5347 | 0.00% | 3.94/sec |
| 15 | 300 | 600 | 544 | 426 | 869 | 1015 | 1694 | 320 | 2974 | 0.00% | 5.00/sec |
| 16 | 320 | 635 | 799 | 466 | 1458 | 2656 | 4824 | 323 | 5454 | 0.00% | 5.28/sec |
| 18 | 360 | 437 | 2737 | 447 | 3463 | 6778 | 44875 | 323 | 47652 | 0.00% | 3.65/sec |

## Notes

- Running JMeter directly on Helios removes the network segment from the local Mac to Helios, so average and median response times are more representative for the internal network.
- Rare max response time spikes still appear even from Helios. Because of that, strict interpretation of `Maximum <= 570 ms` is not satisfied by config 3, but `Average <= 570 ms` is satisfied.
- Config 3 is the best configuration in the Helios run: it is the only one that is close to the required 240 requests/min and has average response time below 570 ms.
- Configs 1 and 2 do not sustain the required throughput in the Helios run: both stay near 180-185 requests/min with average response time above 3 seconds.
- The clean sequential run shows that results are sensitive to run order and previous load. Config 3 became much worse when it was executed after configs 1 and 2, so a 60 second cooldown is likely not enough for the service to return to a clean state.
- For final conclusions, prefer isolated runs with no parallel local GUI test and a longer cooldown between configurations, or repeat each configuration several times and compare medians.
- The clean fresh config 2 rerun used a new `.jtl` file after deleting old results. It still degraded during the run, which means the low sample count is caused by slow responses and blocked threads, not by mixed JMeter result tables.
