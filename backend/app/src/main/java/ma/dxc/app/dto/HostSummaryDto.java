package ma.dxc.app.dto;

import ma.dxc.app.entity.Os;

public record HostSummaryDto(Long id, String ip, String dns, Os os) {
}
