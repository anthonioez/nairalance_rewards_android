package com.nairalance.rewards.android.modules.help.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.controls.expandablerecyclerview.models.ExpandableGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FaqItem extends ExpandableGroup<FaqItem.FaqAnswer>
{
    private long id;
    private String question;
    private int order;

    public FaqItem(long id, String question, List<FaqAnswer> items)
    {
        super(question, items);

        this.id = id;
        this.question = question;
        this.order = 0;
    }

    public FaqItem()
    {
        super("", null);

    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof FaqItem))
        {
            return false;
        }

        FaqItem genre = (FaqItem) o;

        return question == genre.question;

    }

    @Override
    public int hashCode()
    {
        return question.hashCode();
    }

    public static FaqItem copyJSON(JSONObject json)
    {
        try
        {
            FaqItem item = new FaqItem();

            long id = json.getLong(ServerData.id);
            String question = json.getString(ServerData.question);
            //int order = json.optInt(ServerData.order);

            List<FaqAnswer> answers = new ArrayList<>();
            answers.add(item.newAnswer(json.getString(ServerData.answer)));

            return new FaqItem(id, question, answers);
        }
        catch (Exception e) {

        }
        return null;
    }

    public FaqAnswer newAnswer(String queen)
    {
        FaqAnswer answer = new FaqAnswer(queen);
        return answer;
    }

    public class FaqAnswer implements Parcelable
    {
        private String answer;

        public FaqAnswer(String name)
        {
            this.answer = name;
        }

        protected FaqAnswer(Parcel in)
        {
            answer = in.readString();
        }

        public String getAnswer()
        {
            return answer;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FaqAnswer)) {
                return false;
            }

            FaqAnswer answer = (FaqAnswer) o;

            return getAnswer() != null ? getAnswer().equals(answer.getAnswer()) : answer.getAnswer() == null;

        }

        @Override
        public int hashCode()
        {
            int result = getAnswer() != null ? getAnswer().hashCode() : 0;
            return result;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(answer);
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public final Creator<FaqAnswer> CREATOR = new Creator<FaqAnswer>()
        {
            @Override
            public FaqAnswer createFromParcel(Parcel in)
            {
                return new FaqAnswer(in);
            }

            @Override
            public FaqAnswer[] newArray(int size)
            {
                return new FaqAnswer[size];
            }
        };
    }


}

