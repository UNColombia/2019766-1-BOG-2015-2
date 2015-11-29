//
// Created by jefferson on 23/10/15.
//

#include "Distance.h"
#include <cmath>

double Distance::Tanimoto(Individual a, Individual b) {
    double p, q, m;
    p = q = m = 0.0;
    for (int i = 0; i < DIM; ++i) {
        p += a.x[i];
        q += b.x[i];
        m += fmin(a.x[i], b.x[i]);
    }

    return (p + q - 2 * m) / (p + q - m);
}

double Distance::Intersection(Individual a, Individual b) {
    double ans = 0.0;
    for (int i = 0; i < DIM; ++i) {
        ans += fmin(a.x[i], b.x[i]);
    }

    return 1 - ans;
}
