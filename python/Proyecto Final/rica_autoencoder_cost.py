import numpy as np

def normalize(patches):
    ##================================================================
    ## Step 0b: Zero-mean the data (by row)
    #  You can make use of the mean and repmat/bsxfun functions.

    # patches = patches - patches.mean(axis=0)
    patch_mean = patches.mean(axis=1)
    patches = patches - np.tile(patch_mean, (patches.shape[1], 1)).transpose()

    ##================================================================
    ## Step 1a: Implement PCA to obtain xRot
    #  Implement PCA to obtain xRot, the matrix in which the data is expressed
    #  with respect to the eigenbasis of sigma, which is the matrix U.

    sigma = patches.dot(patches.transpose()) / patches.shape[1]
    (u, s, v) = np.linalg.svd(sigma)

    patches_rot = u.transpose().dot(patches)

    ##================================================================
    ## Step 4a: Implement PCA with whitening and regularisation
    #  Implement PCA with whitening and regularisation to produce the matrix
    #  xPCAWhite.

    epsilon = 0.1
    patches_pcawhite = np.diag(1 / (s + epsilon)).dot(patches_rot)


    ##================================================================
    ## Step 5: Implement ZCA whitening
    #  Now implement ZCA whitening to produce the matrix xZCAWhite.
    #  Visualise the data and compare it to the raw data. You should observe
    #  that whitening results in, among other things, enhanced edges.

    patches_zcawhite = u.dot(patches_pcawhite)

    # normalize

    m = np.sqrt(np.sum(np.power(patches_zcawhite, 2), 0) + 1e-8)
    return patches_zcawhite / m


def dot_mul(a, b):
    if a.shape[0] != b.shape[0]:
        raise Exception("error")
    b1 = b.A1
    return np.matrix([elem.A1 if hasattr(elem, 'A1') else elem for elem in [(a[i] * b1[i]) for i in xrange(a.shape[0])]])

# Theta weights initialize
def initialize(rows, cols):
    return np.random.uniform(-0.1, 0.1, rows * cols)


# l2 norm for cost
def l2_row_scaled(x, alpha):
    epsilon = 1e-5
    sum_sq = np.sum(np.power(x, 2), 1) + epsilon

    l2_rows = np.sqrt(sum_sq) * alpha
    return (x.T / l2_rows).T

#l2 norm for derivative
def l2_row_scaled_deriv(x, y, outderv, alpha):
    epsilon = 1e-5

    epsilon_sum_sq = np.sum(np.power(x, 2), 1) + epsilon
    l2_rows = np.sqrt(epsilon_sum_sq) * alpha

    outderv = np.matrix(outderv)

    return (outderv.T / l2_rows).T - dot_mul(y, (np.sum(np.multiply(outderv, x), 1) / epsilon_sum_sq).diagonal().T) # diagonal before transpose

def rica_autoencoder_cost1(theta, x, rows, cols, lambda_, epsilon):
    W = np.reshape(theta, (rows, cols)).T

    W_old = W
    W = l2_row_scaled(W, 1)

    cost = lambda_ * np.sum(np.sum(np.sqrt((np.power(np.dot(W, x), 2)) + epsilon))) + (1/2.0) * np.sum(np.sum(np.power(np.dot(np.dot(W.T, W), x) - x, 2))) / x.shape[1]

    W_grad = lambda_ * np.dot(np.dot(W, x) / np.sqrt(np.power(np.dot(W, x), 2) + epsilon), x.T) + \
             (np.dot(np.dot(W, (np.dot(np.dot(W.T, W), x) - x)), x.T) + np.dot(np.dot(W, x), (np.dot(np.dot(W.T, W), x) - x).T)) / x.shape[1]

    #TODO: must be careful with the way of the return gradient
    grad = l2_row_scaled_deriv(W_old, W, W_grad, 1).T.flatten()

    return cost, grad

a = np.array([6, 5, 4, 5, 4, 3, 4, 3, 2, 3, 2, 1]).T

#rica_autoencoder_cost1(a, np.matrix('1 2; 3 4; 5 6; 7 8'), 3, 4, 0.003, np.finfo(float).eps)