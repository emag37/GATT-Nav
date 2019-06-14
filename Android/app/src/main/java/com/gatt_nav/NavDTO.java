package com.gatt_nav;

class NavDTO {
    public final float distToDest;
    public final float angleToDest;
    public final float speed;

    public NavDTO(float distToDest, float angleToDest, float speed) {
        this.distToDest = distToDest;
        this.angleToDest = angleToDest;
        this.speed = speed;
    }
}
