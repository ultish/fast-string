Testing String implementations for zero-copy

Construction - String: 17755958 ns, FastString: 9666250 ns, FastStringRopeLike: 9973167 ns, Ratio (FS/S): 0.54, Ratio (
FSRL/S): 0.56
Length - String: 2138583 ns, FastString: 968833 ns, FastStringRopeLike: 1036500 ns, Ratio (FS/S): 0.45, Ratio (FSRL/S):
0.48
ToString - String: 1774292 ns, FastString: 16372958 ns, FastStringRopeLike: 25644625 ns, Ratio (FS/S): 9.23, Ratio (
FSRL/S): 14.45
CharAt (mid) - String: 2416041 ns, FastString: 7530291 ns, FastStringRopeLike: 7713375 ns, Ratio (FS/S): 3.12, Ratio (
FSRL/S): 3.19
Substring - String: 8780375 ns, FastString: 11044291 ns, FastStringRopeLike: 16838625 ns, Ratio (FS/S): 1.26, Ratio (
FSRL/S): 1.92
Concat - String: 664834 ns, FastString: 1163416 ns, FastStringRopeLike: 211084 ns, Ratio (FS/S): 1.75, Ratio (FSRL/S):
0.32