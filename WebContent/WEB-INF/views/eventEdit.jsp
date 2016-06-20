<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<jsp:include page="includes/templateStart.jsp" />
<link
	href="http://www.malot.fr/bootstrap-datetimepicker/bootstrap-datetimepicker/css/bootstrap-datetimepicker.css"
	rel="stylesheet">

<!--  add or edit?  ----------------------------------------------------------- -->
<c:choose>
	<c:when test="${not empty event}">
		<c:set var="legend">Edit Event ${event.id}</c:set>
		<c:set var="formAction">editEvent</c:set>
		<c:set var="readonly">readonly</c:set>
		<c:set var="username">${event.userName}</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="legend">New Event</c:set>
		<c:set var="formAction">addEvent</c:set>
		<c:set var="readonly"></c:set>
		<c:set var="username">${userDetails.username}</c:set>
	</c:otherwise>
</c:choose>
<!--  add or edit?  ----------------------------------------------------------- -->

<div id="page-wrapper">
	<div class="graphs">
		<h3 class="blank1">${legend}</h3>
		<div class="xs tabls">
			<div class="bs-example4" data-example-id="contextual-table">

				<table class="table">
					<form class="form-horizontal" method="post" action="${formAction}">
						<fieldset>


							<!-- ----------------  Id ---------------- -->
							<div class="form-group">
								<label for="inputId" class="col-md-2 control-label">Id</label>
								<div class="col-md-10">
									<input class="form-control" id="inputId" type="text" name="id"
										${readonly} value="<c:out value="${event.id}"/>">
								</div>
							</div>


							<!-- ----------------  Title ---------------- -->
							<div class="form-group">
								<label for="inputTitle" class="col-md-2 control-label">Title</label>
								<div class="col-md-10">
									<input class="form-control" id="inputTitle" type="text"
										name="title" value="<c:out value="${event.title}"/>">
								</div>
							</div>
							
							

							<!-- ----------------  start ---------------- -->
							<div class="form-group">
								<label for="inputStart" class="col-md-2 control-label">Start date</label>
								<div class="col-md-10">
									<input class="form_datetime" id="inputStart"
										placeholder="Start Date" type="text" name="start"
										value="<fmt:formatDate value="${event.start}" pattern="dd.MM.yyyy HH:mm"/>">
								</div>
							</div>

							<!--  ----------------  end ---------------- -->
							<div class="form-group">
								<label for="inputEnd" class="col-md-2 control-label">End date</label>
								<div class="col-md-10">
									<input class="form_datetime" id="inputEnd"
										placeholder="Start Date" type="text" name="end"
										value="<fmt:formatDate value="${event.end}" pattern="dd.MM.yyyy HH:mm"/>">
								</div>
							</div>

							<!-- ----------------  Description ---------------- -->
							<div class="form-group">
								<label for="inputDescription" class="col-md-2 control-label">Description</label>
								<div class="col-md-10">
									<input class="form-control" id="inputDescription" type="text"
										name="description"
										value="<c:out value="${event.description}"/>">
								</div>
							</div>

							<!-- ----------------  place ---------------- -->
							<div class="form-group">
								<label for="inputPlace" class="col-md-2 control-label">Place</label>
								<div class="col-md-10">
									<input class="form-control" id="inputPlace" type="text"
										name="place"
										value="<c:out value="${event.place}"/>">
								</div>
							</div>

							<!-- ----------------  userName ---------------- -->
							<div class="form-group">
								<label for="inputUserName" class="col-md-2 control-label">userName</label>
								<div class="col-md-10">
									<input class="form-control" id="inputUserName" type="text"
										name="userName"
										value="<c:out value="${username}"/>">
								</div>
							</div>


							<!-- ----------------  buttons ---------------- -->
							<div class="form-group">
								<div class="col-md-10 col-md-offset-2">
									<button type="submit" class="btn btn-primary">Submit</button>
									<a href="task">
										<button type="button" class="btn btn-default">Cancel</button>
									</a>
								</div>
							</div>
						</fieldset>
						<input type="hidden" name="${_csrf.parameterName }" value="${_csrf.token }"/>
					</form>
				</table>
			</div>

			<!-- /.table-responsive -->
		</div>
	</div>
</div>
<!-- JS for Datetime picker -->

<script type="text/javascript"
	src="http://www.malot.fr/bootstrap-datetimepicker/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js"></script>

<script>
	$(function() {

		$(".form_datetime").datetimepicker({
			format : "dd.mm.yyyy hh:ii",
			autoclose : true,
			todayBtn : true,
			pickerPosition : "bottom-left",
			minuteStep: 5	
		});

	});
</script>


<jsp:include page="includes/templateEnd.jsp" />