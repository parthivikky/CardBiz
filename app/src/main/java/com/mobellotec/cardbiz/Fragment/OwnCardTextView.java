package com.mobellotec.cardbiz.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.Card;
import com.mobellotec.cardbiz.Model.ImageLoadedCallback;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.SelectableRoundedImageView;
import com.mobellotec.cardbiz.Utility.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by MobelloTech on 23-10-2015.
 */
public class OwnCardTextView extends Fragment {

    private Card card;
    private ProgressBar personProgressBar, companyProgressBar;
    private TextView userName, userPhone, userEmail, userRole, userFb, userTwit, userLI, companyName, companyEmail, companyPhone, companyWebsite, companyFax, block, street,
            city, country, postalCode, companyFb, companyTwit, companyLI;
    private SelectableRoundedImageView userImage, companyImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_text_view, container, false);
        try {
            init(view);
            card = getArguments().getParcelable("card");
            display(card);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
        return view;
    }

    private void init(View view) {
        userName = (TextView) view.findViewById(R.id.txt_user_name);
        userPhone = (TextView) view.findViewById(R.id.txt_phone);
        userEmail = (TextView) view.findViewById(R.id.txt_email);
        userRole = (TextView) view.findViewById(R.id.txt_role);
        userFb = (TextView) view.findViewById(R.id.txt_user_fb);
        userTwit = (TextView) view.findViewById(R.id.txt_user_twit);
        userLI = (TextView) view.findViewById(R.id.txt_user_li);
        userImage = (SelectableRoundedImageView) view.findViewById(R.id.user_image);
        companyImage = (SelectableRoundedImageView) view.findViewById(R.id.company_image);
        companyName = (TextView) view.findViewById(R.id.txt_company_name);
        companyEmail = (TextView) view.findViewById(R.id.txt_comp_email);
        companyPhone = (TextView) view.findViewById(R.id.txt_comp_phone);
        companyWebsite = (TextView) view.findViewById(R.id.txt_comp_website);
        companyFax = (TextView) view.findViewById(R.id.txt_comp_fax);
        block = (TextView) view.findViewById(R.id.txt_comp_block);
        street = (TextView) view.findViewById(R.id.txt_comp_street_no);
        city = (TextView) view.findViewById(R.id.txt_comp_city);
        country = (TextView) view.findViewById(R.id.txt_comp_country);
        postalCode = (TextView) view.findViewById(R.id.txt_comp_postal_code);
        companyFb = (TextView) view.findViewById(R.id.txt_comp_fb);
        companyTwit = (TextView) view.findViewById(R.id.txt_comp_twit);
        companyLI = (TextView) view.findViewById(R.id.txt_comp_li);
        personProgressBar = (ProgressBar) view.findViewById(R.id.user_progress_bar);
        companyProgressBar = (ProgressBar) view.findViewById(R.id.comp_progress_bar);
    }

    private void display(Card card) {
        try {
            if (card != null) {
                String strUserImage = card.getPersonImage();
                String strCompanyImage = card.getCompanyImage();
                userName.setText(card.getFirstName() + " " + card.getLastName());
                userPhone.setText("Phone Number : " + card.getMobile());
                userEmail.setText("Email ID : " + card.getEmail());
                userRole.setText("Role In Company : " + card.getRole());
                userFb.setText("Facebook ID : " + card.getPersonFacebook());
                userTwit.setText("Twitter ID : " + card.getPersonTwitter());
                userLI.setText("LinkedIn ID : " + card.getPersonLinkenin());
                companyName.setText(card.getCompanyName());
                companyEmail.setText("Company Email : " + card.getOfficeMail());
                companyPhone.setText("Phone : " + card.getLandline());
                companyWebsite.setText("Website : " + card.getWebsite());
                companyFax.setText("Fax : " + card.getFax());
                block.setText("Block : " + card.getBlock());
                street.setText("Street Name : " + card.getStreet());
                city.setText("City : " + card.getCity());
                country.setText("Country : " + card.getCountry());
                postalCode.setText("Postal Code : " + card.getPostalCode());
                companyFb.setText("Company Facebook : " + card.getCompanyFacebook());
                companyTwit.setText("Company Twitter : " + card.getCompanyTwitter());
                companyLI.setText("Company LinkedIn : " + card.getCompanyLinkedin());
                if (strUserImage.length() > 0) {
                    Picasso.with(getActivity()).load(strUserImage).centerCrop().resize(150, 150).into(userImage, new ImageLoadedCallback(personProgressBar));
                } else {
                    personProgressBar.setVisibility(View.GONE);
                }
                if (strCompanyImage.length() > 0) {
                    Picasso.with(getActivity()).load(strCompanyImage).centerCrop().resize(150, 150).into(companyImage, new ImageLoadedCallback(companyProgressBar));
                } else {
                    companyProgressBar.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }
}
