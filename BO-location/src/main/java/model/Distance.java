package model;

public class Distance {
    private Integer id;
    private Integer fromLieuId;
    private Integer toLieuId;
    private Double distance;

    public Distance() {
    }

    public Distance(Integer id, Integer fromLieuId, Integer toLieuId, Double distance) {
        this.id = id;
        this.fromLieuId = fromLieuId;
        this.toLieuId = toLieuId;
        this.distance = distance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromLieuId() {
        return fromLieuId;
    }

    public void setFromLieuId(Integer fromLieuId) {
        this.fromLieuId = fromLieuId;
    }

    public Integer getToLieuId() {
        return toLieuId;
    }

    public void setToLieuId(Integer toLieuId) {
        this.toLieuId = toLieuId;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "id=" + id +
                ", fromLieuId=" + fromLieuId +
                ", toLieuId=" + toLieuId +
                ", distance=" + distance +
                '}';
    }
}
