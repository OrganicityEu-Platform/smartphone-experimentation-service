package gr.cti.android.experimentation.controller.exception;

/*-
 * #%L
 * Smartphone Experimentation Web Service
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 - 2016 CTI - Computer Technology Institute and Press "Diophantus"
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import gr.cti.android.experimentation.exception.NotAuthorizedException;
import gr.cti.android.experimentation.exception.PluginNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlingController {

    // Exception handling methods

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Not Authorized")  // 401
    @ExceptionHandler(NotAuthorizedException.class)
    public void conflict() {
        // Nothing to do
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Plugin Not found")  // 404
    @ExceptionHandler(PluginNotFoundException.class)
    public void pluginNotFound() {
        // Nothing to do
    }

}