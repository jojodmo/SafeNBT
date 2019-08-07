/*

   Copyright 2019 jojodmo

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package com.jojodmo.safeNBT.api;

import com.jojodmo.safeNBT.Main;
import io.netty.util.internal.UnstableApi;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.AbstractList;

public class SafeNBTList{

    private static final String version = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static final String cbVersion = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static Class<?> tagListClass;
    private static Class<?> nbtBaseClass;

    private final Object tagList;

    static{
        try{
            tagListClass = Class.forName(version + ".NBTTagList");
            nbtBaseClass = Class.forName(version + ".NBTBase");
        }
        catch(Exception ex){
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Main.that);
        }
    }

    @UnstableApi
    public Object getTagList(){
        return tagList;
    }

    public SafeNBTList(){
        this(null);
    }

    public SafeNBTList(Object tagCompound){
        Object toSet = tagCompound;
        if(tagCompound == null){
            try{
                toSet = tagListClass.newInstance();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        this.tagList = toSet;
    }

    public boolean isEmpty(){
        try{
            Method m = tagListClass.getMethod("isEmpty");
            m.setAccessible(true);
            Object r = m.invoke(this.tagList);
            m.setAccessible(false);
            return r instanceof Boolean ? (Boolean) r : true;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return true;
        }
    }

    public void add(SafeNBT value){
        add(value.getTagCompund());
    }

    public <T> void add(SafeNBTBaseType type, T value){
        add(type.make(value));
    }

    public <T> void add(SafeNBTBaseType type, T... values){
        for(T value : values){
            add(type, value);
        }
    }

    public <T> void addGeneric(T value){
        if(value == null){return;}
        SafeNBTBaseType type = SafeNBTBaseType.get(value.getClass());
        if(type == null){return;}
        add(type, value);
    }

    public <T> void add(T... values){
        SafeNBTBaseType type = values.length > 0 ? SafeNBTBaseType.getByClass(values[0].getClass()) : null;
        if(type != null){
            add(type, values);
        }
    }

    private void add(Object nbt){
        try{
            Method m = AbstractList.class.getMethod("add", Object.class);
            m.setAccessible(true);
            m.invoke(tagList, nbt);
            m.setAccessible(false);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
