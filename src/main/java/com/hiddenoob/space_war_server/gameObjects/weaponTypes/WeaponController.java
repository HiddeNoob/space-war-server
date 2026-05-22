package com.hiddenoob.space_war_server.gameObjects.weaponTypes;

import java.util.List;

public class WeaponController {
        private List<Weapon> weapons;
        private int selectedIndex = 0;
        
        void selectWeapon(int index){
            if(index < 0 || index >= weapons.size()) throw new IndexOutOfBoundsException("Böyle bir silah yok");
            selectedIndex = index;
        }

        Weapon getCurrentWeapon(){
            return weapons.get(selectedIndex);
        }
}
