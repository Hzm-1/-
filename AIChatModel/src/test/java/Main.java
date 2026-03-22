import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest
public class Main {
    public static void main(String[] args) {
        String s = "123456789";
        String[] split = s.split("");
        List<List<Integer>> res = new ArrayList<>();
        res.sort(new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                return 0;
            }
        });
    }


    public void dfs(List<List<String>> result,Map<Integer,Integer> map,Map<Integer,Integer> map1,int n){
        for(int j=0;j<n;j++){
            if(map.containsKey(j)){
                continue;
            }
            for(int i=0;i<n;i++){
                if(map.containsKey(j)){
                    continue;
                }
                if(map1.containsKey(i)){
                    continue;
                }
                AtomicBoolean isR = new AtomicBoolean(true);
                int finalJ = j;
                int finalI = i;
                map.forEach((k, v)->{
                    if(Math.abs(k- finalJ)==Math.abs(v- finalI)){
                        isR.set(false);
                    }
                });
                if(isR.get()){
                    map.put(j,i);
                    map1.put(i,j);
                    dfs(result,map,map1,n);
                    map.remove(j);
                    map1.remove(i);
                }
            }
            if(!map.containsKey(j)){
                return;
            }
        }
        List<String> res = new ArrayList<>();
        for(int j=0;j<n;j++){
            StringBuilder s = new StringBuilder("");
            for(int z=0;z<n;z++){
                if(z==map1.get(j)){
                    s.append("Q");
                }else{
                    s.append(".");
                }
            }
            res.add(s.toString());
        }
        if(res.size()==n){
            result.add(new ArrayList<>(res));
        }
        return;
    }
}
