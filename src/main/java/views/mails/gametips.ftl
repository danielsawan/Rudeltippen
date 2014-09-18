<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
</head>
<body>
	<p>${i18n.get('hello')} <#if (user.username)??>${user.username}</#if>!</p>
	<p>${i18n.get('mail.gametips')}</p>
	<table>
		<#list games as game>
			<tr>
				<td colspan="2"><strong>${i18n.get(game.homeTeam.name)} - ${i18n.get(game.awayTeam.name)}</strong></td>
			</tr>
			<#list game.gameTips as gameTip>
				<tr>
					<td>${gameTip.user.username}</td>
					<td>${gameTip.homeScore} : ${gameTip.awayScore}</td>
				</tr>			
			</#list>
		</#list>
		<tr><td>&nbsp;</td></tr>
	</table>
	<p>
	--- <br>
	<#include "footer.ftl">
	</p>
<body>
</html>