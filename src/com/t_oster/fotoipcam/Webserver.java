This file is part of VisiCut.

    VisiCut is free software: you can redistribute it and/or modify
    it under the terms of the Lesser GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    VisiCut is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Lesser GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with VisiCut.  If not, see <http://www.gnu.org/licenses/>.
package com.t_oster.fotoipcam;

	import java.io.*;
import java.util.*;

import android.hardware.Camera;

	/**
	 * An example of subclassing NanoHTTPD to make a custom HTTP server.
	 */
	public class Webserver extends NanoHTTPD
	{
		private FotoIPCamActivity act;
		public Webserver(FotoIPCamActivity act) throws IOException
		{
			super(8080, new File("/sdcard"));
			this.act = act;
		}

		public Response serve( String uri, String method, Properties header, Properties parms, Properties files )
		{
			byte[] picture = act.takePicture();
			return new NanoHTTPD.Response( HTTP_OK, "image/jpeg", new ByteArrayInputStream(picture));
		}
	}

