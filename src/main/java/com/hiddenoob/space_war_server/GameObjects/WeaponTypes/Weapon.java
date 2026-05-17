package com.hiddenoob.space_war_server.GameObjects.WeaponTypes;

public class Weapon {
    final int damage;
    final int bulletThrustPower;
    Bullet blueprintBullet = new Bullet();

    Weapon(int damage, int bulletThrustPower){
        this.damage = damage;
        this.bulletThrustPower = bulletThrustPower;
    }
}
