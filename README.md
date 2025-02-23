Testing String implementations for zero-copy. JDK 22

## Construction

| Type               | Total Time 1mil iterations |
|--------------------|----------------------------|
| String             | 17755958 ns                |
| FastString         | 9666250 ns                 |
| FastStringRopeLike | 9973167 ns                 |
| Ratio (FS/S)       | 0.54                       |
| Ratio (FSRL/S)     | 0.56                       |

## Length

| Type               | Total Time 1mil iterations |
|--------------------|----------------------------|
| String             | 2138583 ns                 |
| FastString         | 968833 ns                  |
| FastStringRopeLike | 1036500 ns                 |
| Ratio (FS/S)       | 0.45                       |
| Ratio (FSRL/S)     | 0.48                       |

## ToString

| Type               | Total Time 1mil iterations |
|--------------------|----------------------------|
| String             | 1774292 ns                 |
| FastString         | 16372958 ns                |
| FastStringRopeLike | 25644625 ns                |
| Ratio (FS/S)       | 9.23                       |
| Ratio (FSRL/S)     | 14.45                      |

## ToString (FastStringRopeLike modified with cache)

To improve FastStringRopeLike's toString function we introduce a cache for the underlying string
| Type | Total Time 1mil iterations |
|--------------------|----------------------------|
| String | 1995500 ns |
| FastString | 17632917 ns |
| FastStringRopeLike | 2160625 ns |
| Ratio (FS/S)       | 8.84 |
| Ratio (FSRL/S)     | 1.08 |

## CharAt (mid)

| Type               | Total Time 1mil iterations |
|--------------------|----------------------------|
| String             | 2416041 ns                 |
| FastString         | 7530291 ns                 |
| FastStringRopeLike | 7713375 ns                 |
| Ratio (FS/S)       | 3.12                       |
| Ratio (FSRL/S)     | 3.19                       |

## Substring

| Type               | Total Time 1mil iterations |
|--------------------|----------------------------|
| String             | 8780375 ns                 |
| FastString         | 11044291 ns                |
| FastStringRopeLike | 16838625 ns                |
| Ratio (FS/S)       | 1.26                       |
| Ratio (FSRL/S)     | 1.92                       |

## Concat

| Type               | Total Time 10,000 concats |
|--------------------|---------------------------|
| String             | 16634750 ns               |
| FastString         | 82732125 ns               |
| FastStringRopeLike | 831209 ns                 |
| Ratio (FS/S)       | 4.97                      |
| Ratio (FSRL/S)     | 0.05                      |


 