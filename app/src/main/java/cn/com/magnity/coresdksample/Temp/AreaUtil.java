package cn.com.magnity.coresdksample.Temp;

import static cn.com.magnity.coresdksample.Temp.FFCUtil.m_FrameHeight;
import static cn.com.magnity.coresdksample.Temp.FFCUtil.m_FrameWidth;

public class AreaUtil {

    public static int [] AreaLimit(int x0,int x1,int y0,int y1){
        int []result=new int[4];
        if(x0<0){
            x0=0;
        }
        if(x1<0){
            x1=0;
        } if(y0<0){
            y0=0;
        } if(y1<0){
            y1=0;
        }
        if(x0>m_FrameWidth){
            x0=m_FrameWidth;
        }
        if(x1>m_FrameWidth){
            x1=m_FrameWidth;
        } if(y0>m_FrameHeight){
            y0=m_FrameHeight;
        } if(y1>m_FrameHeight){
            y1=m_FrameHeight;
        }
        result[0]=x0;
        result[1]=x1;
        result[2]=y0;
        result[3]=y1;


        return result;
    }
}
