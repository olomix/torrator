<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Start new torrent download.</title>
</head>
<body>
	<h1>Start new torrent download.</h1>
	<form method="post" enctype="multipart/form-data">
		<fieldset>
			<label for="torrentFile">Torrent file:</label> <input
				id="torrentFile" name="torrentFile" type="file" /> <br /> <input
				type="submit" />
		</fieldset>
	</form>
</body>
</html>