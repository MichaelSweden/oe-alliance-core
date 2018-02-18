DESCRIPTION = "Linux kernel for ${MACHINE}"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

KERNEL_RELEASE = "4.4.35"

SRCDATE = "20180214"

inherit kernel machine_kernel_pr

SRC_URI[arm.md5sum] = "bb368255800be3d3d7cfa2710928fe9c"
SRC_URI[arm.sha256sum] = "3dd7e7a99f70f0be8b725e4628f243c3aa1d42072a32e4a4b5268f69b535fc1d"
SRC_URI[part.md5sum] = "850782d98b18406fed30a7b592a45a21"
SRC_URI[part.sha256sum] = "2e632ce9817eab5c7977f8a259bf5b854045ff189ebef3a6641968c946ba83cc"


SRC_URI = "http://source.mynonpublic.com/zgemma/linux-${PV}-${SRCDATE}-${ARCH}.tar.gz;name=${ARCH} \
	http://source.mynonpublic.com/zgemma/${MACHINE}-partitions_20180120.zip;name=part \
	file://defconfig \
	"

# By default, kernel.bbclass modifies package names to allow multiple kernels
# to be installed in parallel. We revert this change and rprovide the versioned
# package names instead, to allow only one kernel to be installed.
PKG_kernel-base = "kernel-base"
PKG_kernel-image = "kernel-image"
RPROVIDES_kernel-base = "kernel-${KERNEL_VERSION}"
RPROVIDES_kernel-image = "kernel-image-${KERNEL_VERSION}"	

S = "${WORKDIR}/linux-${PV}"
B = "${WORKDIR}/build"

export OS = "Linux"
KERNEL_OBJECT_SUFFIX = "ko"
KERNEL_IMAGEDEST = "tmp"
KERNEL_IMAGETYPE = "uImage"
KERNEL_OUTPUT = "arch/${ARCH}/boot/${KERNEL_IMAGETYPE}"

FILES_kernel-image = "/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}"

kernel_do_install_append() {
	install -d ${D}${KERNEL_IMAGEDEST}
	install -m 0755 ${KERNEL_OUTPUT} ${D}${KERNEL_IMAGEDEST}
}

kernel_do_install_append() {
        install -d ${DEPLOY_DIR_IMAGE}
        install -m 0755 ${WORKDIR}/fastboot.bin ${DEPLOY_DIR_IMAGE}
        install -m 0755 ${WORKDIR}/pq_param.bin ${DEPLOY_DIR_IMAGE}
        install -m 0755 ${WORKDIR}/bootargs.bin ${DEPLOY_DIR_IMAGE}
        install -m 0755 ${WORKDIR}/bootargs_flash.bin ${DEPLOY_DIR_IMAGE}
        install -m 0755 ${WORKDIR}/baseparam.img ${DEPLOY_DIR_IMAGE}
        install -m 0755 ${WORKDIR}/logo.img ${DEPLOY_DIR_IMAGE}
}


pkg_postinst_kernel-image() {
	if [ "x$D" == "x" ]; then
		if [ -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE} ] ; then
			flash_eraseall /dev/${MTD_KERNEL}
			nandwrite -p /dev/${MTD_KERNEL} /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}
		fi
	fi
	true
}

do_rm_work() {
}