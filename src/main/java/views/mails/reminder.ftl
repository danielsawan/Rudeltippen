<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
</head>
<body>
	<p>${i18n.get('hello')} <#if (user.username)??>${user.username}</#if>!</p>
	<p>${i18n.get('mail.placetips')}</p>
	<table>
		<tr>
			<td><strong>${i18n.get('mail.games')}</strong></td>
		</tr>
		<#if games??>
			<#list games as game>
				<tr>
					<td>${i18n.get(game.homeTeam.name)} - ${i18n.get(game.awayTeam.name)}</td>
				</tr>
				<tr>
					<td>${i18n.get('mail.tipto')}: ${game.tippEnding()?datetime}</td>
				</tr>
				<#if game_has_next>
					<tr>
						<td>&nbsp;</td>
					</tr>			
				</#if>
			</#list>
		<#else>
			<tr>
				<td>${i18n.get('mail.nogames')}</td>
			</tr>
		</#if>
	</table>
	<br /><br />
	<table>
		<tr>
			<td><strong>${i18n.get('extratips')}</strong></td>
		</tr>
		<#if extras??>
		<#list extras as extra>
			<tr>
				<td>${i18n.get('extra.question')}</td>
			</tr>
			<tr>
				<td>${i18n.get('mail.tipto')}: ${extra.ending?datetime}</td>
			</tr>
			<#if extra_has_next>
				<tr>
					<td>&nbsp;</td>
				</tr>			
			</#if>
		</#list>
		<#else>
			<tr>
				<td>${i18n.get('mail.noextras')}</td>
			</tr>
		</#if>
	</table>
	<p>
	--- <br>
	<#include "footer.ftl">
	</p>
<body>
</html>