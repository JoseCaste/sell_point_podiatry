package com.podiatry.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessCriteria {
	//collection_id=1244785543&collection_status=approved&payment_id=1244785543&status=approved&external_reference=null
	//&payment_type=credit_card&merchant_order_id=3817189628&preference_id=1043363108-ed9c7301-3673-4826-a25f-1a2c817acd1d
	//&site_id=MLM&processing_mode=aggregator&merchant_account_id=null
	private Integer collection_id;
	private String collection_status;
	private Integer payment_id;
	private String status;
	private String external_reference;
	private String payment_type;
	private String merchant_order_id;
	private String preference_id;
	private String site_id;
	private String processing_mode;
	private String merchant_id;
	public SuccessCriteria() {
	}
	
	
}
