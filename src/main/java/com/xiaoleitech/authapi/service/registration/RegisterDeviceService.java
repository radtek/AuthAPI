package com.xiaoleitech.authapi.service.registration;

import com.xiaoleitech.authapi.model.bean.RegisterDeviceRequest;
import com.xiaoleitech.authapi.model.bean.RegisterDeviceResponse;
import com.xiaoleitech.authapi.model.bean.UnregisterDeviceResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

public interface RegisterDeviceService {
    /** 注册设备
     * post https://server/api/register_device
     * @param registerDeviceRequest Post data of register_device API.
     * 		form data:
     * 			imei=<imei>
     * 			protect_method_capability=<protect_method_capability>
     * 			device_type=<device_type>
     * 			device_model=<device_model>
     * 			device_tee=<device_tee>
     * 			device_se=<device_se>
     * 			device_token=<device_token>
     * @param bindingResult         Data binding result, including the validation error info if any.
     * @return Response of the register_device API.
     * 		return:
     *                        {
     * 				error_code: errercode,
     * 				error_message: error_message,
     * 				[device_id: device_id]  // if errorcode == 0
     *            }
     */
    RegisterDeviceResponse registerDevcie(RegisterDeviceRequest registerDeviceRequest, BindingResult bindingResult);

    /**
     * 设备反注册 (APP)
     * get https://server/api/deregister_device?device_id=<device_id>
     *
     * @param deviceID 系统定义的UUID，存于devices主表中
     * @return {
     * error_code: errercode,
     * error_message: error_message
     * }
     */
    UnregisterDeviceResponse unregisterDevice(@RequestParam("device_id") String deviceID);

}
