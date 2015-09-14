/**
 * Copyright (C) 2014 Seagate Technology.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.seagate.kinetic.console.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.seagate.kinetic.console.service.ConsoleService;
import com.seagate.kinetic.tools.management.rest.message.hwview.HardwareViewResponse;

public class ConsoleServlet extends HttpServlet {

	private static final long serialVersionUID = -2034467086492824589L;
	private ConsoleService consoleService = null;
	private Gson gson = new Gson();

	public ConsoleServlet() {}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");

		String responseMessage = "";

		if (action.equalsIgnoreCase("enabledebug")) {
		    if (consoleService != null)
			    consoleService.enableDebugMode();
		} else if (action.equalsIgnoreCase("disabledebug")) {
		    if (consoleService != null)
		        consoleService.disableDebugMode();
		} else if (action.equalsIgnoreCase("liststate")) {
		    if (consoleService != null)
		        responseMessage = consoleService.listDevicesState();
		} else if (action.equalsIgnoreCase("dscdevice")) {
		    if (consoleService != null)
		    {
		        String wwn = req.getParameter("wwn");
	            responseMessage = consoleService.describeDevice(wwn);
		    }
		} else if (action.equalsIgnoreCase("listhwfiles")) {
			List<String> files = ConsoleService.listHwViewFiles();
			responseMessage += "[";
			for (int i = 0; i < files.size(); i++) {
				responseMessage += "\"";
				responseMessage += files.get(i);
				responseMessage += "\"";

				if (i < files.size() - 1) {
					responseMessage += ",";
				}
			}
			responseMessage += "]";
		} else if (action.equalsIgnoreCase("selecthwvfile"))
		{
		    if (null == consoleService) {
                synchronized (this) {
                    if (null == consoleService) {
                        String fileName = req.getParameter("filename");
                        consoleService = new ConsoleService(fileName);
                    }
                }
            }
		} else if (action.equalsIgnoreCase("gethwv"))
		{
		    if (consoleService != null)
		    {
		        HardwareViewResponse hardwareViewResponse = new HardwareViewResponse();
		        hardwareViewResponse.setHardwareView(consoleService.getHardwareView());
		        responseMessage += gson.toJson(hardwareViewResponse);
		    }
		}

		Writer writer = resp.getWriter();

		writer.write(responseMessage);

		writer.close();

	}

}
