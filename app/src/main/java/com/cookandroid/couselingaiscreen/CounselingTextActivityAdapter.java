package com.cookandroid.couselingaiscreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class CounselingTextActivityAdapter extends ArrayAdapter<ResponseMessage> {
    private TextView chatText;
    private ImageView aiEmage;
    private List<ResponseMessage> messageList = new ArrayList<ResponseMessage>(); // stores number of messages
    private Context context; //context:어플리케이션에 대해서 현재 상태를 나타내는 역할

    @Override
    public void add(ResponseMessage object) {  // ArrayList에 아이템을 추가하는 코드입니다.
        messageList.add(object);
        super.add(object);
    }

    // Constructor - take lite
    public CounselingTextActivityAdapter(Context context, int textViewResourceId) {
        //context : 현재의 Context. 일반적으로 Adapter를 포함하는Activity의 instance가 들어간다.
        //int textViewResourceId : TextView를 포함하는 layout 파일의 resource ID. 각 항목들을 어떤 형식으로 나타낼 것인지 결정해준다.
        super(context, textViewResourceId);
        this.context = context;
    }

    // returns total number of messages
    public int getCount() {   //ListView에서 사용할 데이터의 총개수
        return this.messageList.size();
    }
    public static final int SEND = 0;  //add
    public static final int receive = 0;   //add

    // returns message at an index
    public ResponseMessage getItem(int index) {
        return this.messageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {   // Adapter 가 가지고 있는 data 를 어떻게 보여줄 것인가를 정의
        //int position : 아이템의 인덱스를 의미하는 것으로 리스트뷰에서 보일 아이템의 위치 정보이다. 0부터 시작하여 아이템 개수만큼 파라미터로 전달된다.
        //View convertView : 현재 인덱스에 해당하는 뷰 객체를 의미
        //ViewGroup parent : 이 뷰를 포함하고 있는 부모 컨테이너 객체이다.
        ResponseMessage messageObj = getItem(position);
        View row = convertView; //ListView adapter에서 이전 뷰가 재사용이 가능할 경우, 그 뷰 정보.
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //LayoutInvflater : xml에 정의된 Resource 를 View 객체로 반환해 주는 역할을 한다.
        //getContext() : 현재 액티비티의 Context를 얻는다.
        //getSystemService() : 시스템에서 제공하는 디바이스나 안드로이드 프레임워크내 기능을 다른 애플리케이션과 공유하고자 하는 시스템으로 부터 객체 얻을 때 사용
        if (messageObj.isMe()) {
            row = inflater.inflate(R.layout.counselingtextscreen_send, parent, false);
            //inflater : xml로 정의된 view나 menu등을 실제 객체화 시키는 역할
            chatText = (TextView) row.findViewById(R.id.msgr);
            chatText.setText(messageObj.getTextMessage());
        } else {
            row = inflater.inflate(R.layout.counselingtextscreen_receive, parent, false);
            chatText = (TextView) row.findViewById(R.id.msgr);
           // aiEmage = (ImageView) row.findViewById(R.id.AI_image);
            chatText.setText(messageObj.getTextMessage());
        }
        //chatText = (TextView) row.findViewById(R.id.msgr);
        //aiEmage = (ImageView) row.findViewById(R.id.AI_image);
        //chatText.setText(messageObj.getTextMessage());
        return row;
    }
}
