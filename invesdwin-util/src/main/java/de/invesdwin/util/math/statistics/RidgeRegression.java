package de.invesdwin.util.math.statistics;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 * https://github.com/Netflix/Surus/blob/master/src/main/java/org/surus/math/RidgeRegression.java
 */
@NotThreadSafe
public class RidgeRegression {

    private final RealMatrix mX;
    private SingularValueDecomposition mX_svd = null;
    private final double[] mY;
    private double l2penalty;
    private double[] coefficients;
    private double[] standarderrors;

    private double[] fitted;
    private final double[] residuals;

    public RidgeRegression(final double[][] x, final double[] y) {
        this.mX = MatrixUtils.createRealMatrix(x);
        this.mX_svd = null;
        this.mY = y;
        this.l2penalty = 0;
        this.coefficients = null;

        this.fitted = new double[y.length];
        this.residuals = new double[y.length];
    }

    public void updateCoefficients(final double l2penalty) {
        if (this.mX_svd == null) {
            this.mX_svd = new SingularValueDecomposition(mX);
        }
        final RealMatrix mV = this.mX_svd.getV();
        final double[] s = this.mX_svd.getSingularValues();
        final RealMatrix mU = this.mX_svd.getU();

        for (int i = 0; i < s.length; i++) {
            s[i] = s[i] / (s[i] * s[i] + l2penalty);
        }
        final RealMatrix mS = MatrixUtils.createRealDiagonalMatrix(s);

        final RealMatrix mZ = mV.multiply(mS).multiply(mU.transpose());

        this.coefficients = mZ.operate(this.mY);

        this.fitted = this.mX.operate(this.coefficients);
        double errorVariance = 0;
        for (int i = 0; i < residuals.length; i++) {
            this.residuals[i] = this.mY[i] - this.fitted[i];
            errorVariance += this.residuals[i] * this.residuals[i];
        }
        errorVariance = errorVariance / (mX.getRowDimension() - mX.getColumnDimension());

        final RealMatrix errorVarianceMatrix = MatrixUtils.createRealIdentityMatrix(this.mY.length)
                .scalarMultiply(errorVariance);
        final RealMatrix coefficientsCovarianceMatrix = mZ.multiply(errorVarianceMatrix).multiply(mZ.transpose());
        this.standarderrors = getDiagonal(coefficientsCovarianceMatrix);
    }

    private double[] getDiagonal(final RealMatrix mX) {
        final double[] diag = new double[mX.getColumnDimension()];
        for (int i = 0; i < diag.length; i++) {
            diag[i] = mX.getEntry(i, i);
        }
        return diag;
    }

    public double getL2penalty() {
        return l2penalty;
    }

    public void setL2penalty(final double l2penalty) {
        this.l2penalty = l2penalty;
    }

    public double[] getCoefficients() {
        return coefficients;
    }

    public double[] getStandarderrors() {
        return standarderrors;
    }

    public double[] getResiduals() {
        return residuals;
    }
}
