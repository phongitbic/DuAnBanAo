package Model;


/**
 *
 * @author HoaNguyenVan
 */
public class PointsConversion {

    private int scorePointsId;
    private double moneyIntoPoints;
    private int pointsIntoMoney;

    public PointsConversion(double moneyIntoPoints, int pointsIntoMoney) {
        this.moneyIntoPoints = moneyIntoPoints;
        this.pointsIntoMoney = pointsIntoMoney;
    }

    public PointsConversion() {
    }

    public PointsConversion(int scorePointsId, double moneyIntoPoints, int pointsIntoMoney) {
        this.scorePointsId = scorePointsId;
        this.moneyIntoPoints = moneyIntoPoints;
        this.pointsIntoMoney = pointsIntoMoney;
    }

    public int getScorePointsId() {
        return scorePointsId;
    }

    public void setScorePointsId(int scorePointsId) {
        this.scorePointsId = scorePointsId;
    }

    public double getMoneyIntoPoints() {
        return moneyIntoPoints;
    }

    public void setMoneyIntoPoints(double moneyIntoPoints) {
        this.moneyIntoPoints = moneyIntoPoints;
    }

    public int getPointsIntoMoney() {
        return pointsIntoMoney;
    }

    public void setPointsIntoMoney(int pointsIntoMoney) {
        this.pointsIntoMoney = pointsIntoMoney;
    }

}
