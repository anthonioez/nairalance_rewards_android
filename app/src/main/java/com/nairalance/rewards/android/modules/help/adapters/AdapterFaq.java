package com.nairalance.rewards.android.modules.help.adapters;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.miciniti.library.Links;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.modules.help.objects.FaqItem;
import com.nairalance.rewards.android.controls.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.nairalance.rewards.android.controls.expandablerecyclerview.models.ExpandableGroup;
import com.nairalance.rewards.android.controls.expandablerecyclerview.viewholders.ChildViewHolder;
import com.nairalance.rewards.android.controls.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class AdapterFaq extends ExpandableRecyclerViewAdapter<AdapterFaq.AdapterFaqQuestionViewHolder, AdapterFaq.AdapterFaqAnswerViewHolder>
{
    private final Context context;

    public AdapterFaq(Context context, List<? extends ExpandableGroup> groups)
    {
        super(groups);

        this.context = context;
    }

    @Override
    public AdapterFaqQuestionViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_faq_question, parent, false);
        return new AdapterFaqQuestionViewHolder(view);
    }

    @Override
    public AdapterFaqAnswerViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_faq_answer, parent, false);
        return new AdapterFaqAnswerViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(AdapterFaqAnswerViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex)
    {
        final FaqItem.FaqAnswer answer = ((FaqItem) group).getItems().get(childIndex);
        holder.setAnswer(answer.getAnswer());
    }

    @Override
    public void onBindGroupViewHolder(AdapterFaqQuestionViewHolder holder, int flatPosition, ExpandableGroup group)
    {
        holder.setQuestion(group);
    }

    public class AdapterFaqAnswerViewHolder extends ChildViewHolder
    {
        private TextView textAnswer;

        public AdapterFaqAnswerViewHolder(View itemView)
        {
            super(itemView);

            textAnswer = itemView.findViewById(R.id.textAnswer);
            textAnswer.setTypeface(Rewards.appFont);

        }

        public void setAnswer(String content)
        {
            Spanned html;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            {
                html = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
            }
            else
            {
                html = Html.fromHtml(content);
            }

            SpannableStringBuilder strBuilder = new SpannableStringBuilder(html);
            URLSpan[] urls = strBuilder.getSpans(0, html.length(), URLSpan.class);
            for(URLSpan span : urls)
            {
                makeLinkClickable(strBuilder, span);
            }

            textAnswer.setText(strBuilder);
            textAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        }

        private void makeLinkClickable(SpannableStringBuilder builder, final URLSpan span)
        {
            int start = builder.getSpanStart(span);
            int end = builder.getSpanEnd(span);
            int flags = builder.getSpanFlags(span);

            ClickableSpan clickable = new ClickableSpan()
            {
                public void onClick(View view)
                {
                    String url = span.getURL();
                    Links.openExternalUrl(context, url);
                }
            };

            builder.setSpan(clickable, start, end, flags);
            builder.removeSpan(span);
        }
    }

    public class AdapterFaqQuestionViewHolder extends GroupViewHolder
    {
        private TextView textQuestion;
        private ImageView arrow;

        public AdapterFaqQuestionViewHolder(View itemView)
        {
            super(itemView);

            textQuestion    = itemView.findViewById(R.id.textQuestion);
            arrow           = itemView.findViewById(R.id.imageArrow);

            textQuestion.setTypeface(Rewards.appFontBold);
        }

        public void setQuestion(ExpandableGroup genre)
        {
            if (genre instanceof FaqItem)
            {
                textQuestion.setText(genre.getTitle());
            }
        }

        @Override
        public void expand()
        {
            animateExpand();
        }

        @Override
        public void collapse()
        {
            animateCollapse();
        }

        private void animateExpand()
        {
            RotateAnimation rotate = new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }

        private void animateCollapse()
        {
            RotateAnimation rotate = new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }
    }

}
